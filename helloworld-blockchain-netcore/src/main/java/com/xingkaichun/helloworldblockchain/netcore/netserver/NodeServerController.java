package com.xingkaichun.helloworldblockchain.netcore.netserver;

import com.xingkaichun.helloworldblockchain.core.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.request.*;
import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.response.*;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockchainNodeServerService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 负责节点与节点通信的控制器
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class NodeServerController {

    private static final Logger logger = LoggerFactory.getLogger(NodeServerController.class);

    private BlockChainCoreService blockChainCoreService;
    private NodeService nodeService;
    private BlockchainNodeServerService blockchainNodeServerService;
    private ConfigurationService configurationService;

    /**
     * Ping节点
     */
    public ServiceResult<PingResponse> ping(ChannelHandlerContext ctx, PingRequest request){
        try {
            List<Node> nodeList = nodeService.queryAllNoForkNodeList();
            BigInteger blockChainHeight = blockChainCoreService.queryBlockChainHeight();

            //将ping的来路作为区块链节点
            ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.AUTO_SEARCH_NODE.name());
            if(Boolean.valueOf(configurationDto.getConfValue())){
                Node node = new Node();
                InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = insocket.getAddress().getHostAddress();
                node.setIp(ip);
                node.setPort(request.getPort());
               if(nodeService.queryNode(node) == null){
                   node.setIsNodeAvailable(true);
                   nodeService.addNode(node);
                   logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,request.getPort()));
               }
            }

            PingResponse response = new PingResponse();
            response.setNodeList(nodeList);
            response.setBlockChainHeight(blockChainHeight);
            response.setBlockChainId(GlobalSetting.BLOCK_CHAIN_ID);
            response.setBlockChainVersion(GlobalSetting.SystemVersionConstant.obtainVersion());
            return ServiceResult.createSuccessServiceResult("查询节点信息成功",response);
        } catch (Exception e){
            String message = "查询节点信息成功失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 更新节点信息：其它节点通知本地节点它的信息有变更
     */
    public ServiceResult<AddOrUpdateNodeResponse> addOrUpdateNode(ChannelHandlerContext ctx,AddOrUpdateNodeRequest request){
        try {
            Node node = new Node();
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = insocket.getAddress().getHostAddress();
            node.setIp(ip);
            node.setPort(request.getPort());
            node.setIsNodeAvailable(true);
            node.setBlockChainHeight(request.getBlockChainHeight());
            node.setErrorConnectionTimes(0);

            ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.AUTO_SEARCH_NODE.name());
            if(!Boolean.valueOf(configurationDto.getConfValue())){
                Node nodeInDb = nodeService.queryNode(node);
                if(nodeInDb == null){
                    return ServiceResult.createSuccessServiceResult("你不是该节点的授信节点。",null);
                }
                logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,request.getPort()));
            }
            if(nodeService.queryNode(node) == null){
                nodeService.addNode(node);
            }else {
                nodeService.updateNode(node);
            }
            AddOrUpdateNodeResponse response = new AddOrUpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("更新节点成功",response);
        } catch (Exception e){
            String message = "更新节点失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }


    /**
     * 根据区块高度查询区块Hash
     */
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(QueryBlockHashByBlockHeightRequest request){
        try {
            String blockHash = blockChainCoreService.queryBlockHashByBlockHeight(request.getBlockHeight());

            QueryBlockHashByBlockHeightResponse response = new QueryBlockHashByBlockHeightResponse();
            response.setBlockHash(blockHash);
            return ServiceResult.createSuccessServiceResult("成功获取区块Hash",response);
        } catch (Exception e){
            String message = "查询区块Hash失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 根据区块高度查询区块
     */
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(QueryBlockDtoByBlockHeightRequest request){
        try {
            BlockDTO blockDTO = blockChainCoreService.queryBlockDtoByBlockHeight(request.getBlockHeight());

            QueryBlockDtoByBlockHeightResponse response = new QueryBlockDtoByBlockHeightResponse();
            response.setBlockDTO(blockDTO);
            return ServiceResult.createSuccessServiceResult("成功获取区块",response);
        } catch (Exception e){
            String message = "查询获取失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 接收其它节点提交的交易
     */
    public ServiceResult<ReceiveTransactionResponse> receiveTransaction(ReceiveTransactionRequest request){
        try {
            blockchainNodeServerService.receiveTransaction(request.getTransactionDTO());

            ReceiveTransactionResponse response = new ReceiveTransactionResponse();
            return ServiceResult.createSuccessServiceResult("提交交易成功",response);
        } catch (Exception e){
            String message = "提交交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}