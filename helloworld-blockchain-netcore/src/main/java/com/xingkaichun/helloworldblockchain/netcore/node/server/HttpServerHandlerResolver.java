package com.xingkaichun.helloworldblockchain.netcore.node.server;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request.*;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.*;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 节点请求处理器
 *
 * @author 邢开春 409060350@qq.com
 */
public class HttpServerHandlerResolver {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandlerResolver.class);

    private BlockchainCore blockchainCore;
    private NodeService nodeService;
    private ConfigurationService configurationService;

    public HttpServerHandlerResolver(BlockchainCore blockchainCore, NodeService nodeService, ConfigurationService configurationService) {
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
        this.configurationService = configurationService;
    }

    /**
     * Ping节点
     */
    public ServiceResult<PingResponse> ping(ChannelHandlerContext ctx, PingRequest request){
        try {
            List<NodeDto> nodeList = nodeService.queryAllNoForkNodeList();
            long blockchainHeight = blockchainCore.queryBlockchainHeight();

            //将ping的来路作为区块链节点
            NodeDto node = new NodeDto();
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = insocket.getAddress().getHostAddress();
            node.setIp(ip);
            if(nodeService.queryNode(node) == null){
                node.setIsNodeAvailable(true);
                if(configurationService.isAutoSearchNode()){
                    nodeService.addNode(node);
                    logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,GlobalSetting.DEFAULT_PORT));
                }
            }

            PingResponse response = new PingResponse();
            response.setNodeList(nodeList);
            response.setBlockchainHeight(blockchainHeight);
            return ServiceResult.createSuccessServiceResult("ping node info success",response);
        } catch (Exception e){
            String message = "ping node info failed";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 更新节点信息：其它节点通知本地节点它的信息有变更
     */
    public ServiceResult<AddOrUpdateNodeResponse> addOrUpdateNode(ChannelHandlerContext ctx,AddOrUpdateNodeRequest request){
        try {
            NodeDto node = new NodeDto();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = inetSocketAddress.getAddress().getHostAddress();
            node.setIp(ip);
            node.setIsNodeAvailable(true);
            node.setBlockchainHeight(request.getBlockchainHeight());
            node.setErrorConnectionTimes(0);

            if(nodeService.queryNode(node) == null){
                if(configurationService.isAutoSearchNode()){
                    nodeService.addNode(node);
                    logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,GlobalSetting.DEFAULT_PORT));
                    return ServiceResult.createFailServiceResult("节点新增成功");
                }else {
                    return ServiceResult.createFailServiceResult("节点新增失败");
                }
            }else {
                nodeService.updateNode(node);
            }
            AddOrUpdateNodeResponse response = new AddOrUpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("节点更新成功",response);
        } catch (Exception e){
            String message = "节点更新失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }


    /**
     * 根据区块高度查询区块Hash
     */
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(QueryBlockHashByBlockHeightRequest request){
        try {
            String blockHash = blockchainCore.queryBlockHashByBlockHeight(request.getBlockHeight());

            QueryBlockHashByBlockHeightResponse response = new QueryBlockHashByBlockHeightResponse();
            response.setBlockHash(blockHash);
            return ServiceResult.createSuccessServiceResult("query block hash by block height success",response);
        } catch (Exception e){
            String message = "query block hash by block height failed";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 根据区块高度查询区块
     */
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(QueryBlockDtoByBlockHeightRequest request){
        try {
            Block block = blockchainCore.queryBlockByBlockHeight(request.getBlockHeight());
            BlockDTO blockDTO = Model2DtoTool.block2BlockDTO(block);

            QueryBlockDtoByBlockHeightResponse response = new QueryBlockDtoByBlockHeightResponse();
            response.setBlockDTO(blockDTO);
            return ServiceResult.createSuccessServiceResult("query block by block height success",response);
        } catch (Exception e){
            String message = "query block by block height failed";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 接收其它节点提交的交易
     */
    public ServiceResult<SubmitTransactionToNodeResponse> submitTransactionToNodeRequest(SubmitTransactionToNodeRequest request){
        try {
            blockchainCore.submitTransaction(request.getTransactionDTO());

            SubmitTransactionToNodeResponse response = new SubmitTransactionToNodeResponse();
            return ServiceResult.createSuccessServiceResult("commit transaction success",response);
        } catch (Exception e){
            String message = "commit transaction failed";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}