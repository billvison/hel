package com.xingkaichun.helloworldblockchain.netcore.server;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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
    public String ping(ChannelHandlerContext ctx, PingRequest request){
        try {
            List<NodeEntity> nodeList = nodeService.queryAllNodeList();
            long blockchainHeight = blockchainCore.queryBlockchainHeight();

            //将ping的来路作为区块链节点
            NodeEntity node = new NodeEntity();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = inetSocketAddress.getAddress().getHostAddress();
            node.setIp(ip);
            if(request != null){
                if(request.getBlockchainHeight() != null){
                    node.setBlockchainHeight(request.getBlockchainHeight());
                }
            }

            if(nodeService.queryNode(new NodeDTO(node.getIp())) == null){
                if(configurationService.isAutoSearchNode()){
                    nodeService.addNode(node);
                    logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,GlobalSetting.DEFAULT_PORT));
                }
            }else {
                nodeService.updateNode(node);
            }

            List<NodeDTO> nodeDtoList = new ArrayList<>();
            if(nodeList != null){
                for(NodeEntity n:nodeList){
                    NodeDTO b = new NodeDTO();
                    b.setIp(n.getIp());
                    nodeDtoList.add(b);
                }
            }

            PingResponse response = new PingResponse();
            response.setNodes(nodeDtoList);
            response.setBlockchainHeight(blockchainHeight);
            return new Gson().toJson(response);
        } catch (Exception e){
            String message = "ping node info failed";
            logger.error(message,e);
            return API.Response.ERROR;
        }
    }


    /**
     * 根据区块高度查询区块
     */
    public String getBlock(long height){
        try {
            Block blockByBlockHeight = blockchainCore.queryBlockByBlockHeight(height);
            BlockDTO block = Model2DtoTool.block2BlockDTO(blockByBlockHeight);
            return new Gson().toJson(block);
        } catch (Exception e){
            String message = "query block by block height failed";
            logger.error(message,e);
            return API.Response.ERROR;
        }
    }

    /**
     * 接收其它节点提交的交易
     */
    public String postTransaction(TransactionDTO transactionDTO){
        try {
            blockchainCore.submitTransaction(transactionDTO);
            return API.Response.OK;
        } catch (Exception e){
            String message = "commit transaction failed";
            logger.error(message,e);
            return API.Response.ERROR;
        }
    }

    public String postBlock(BlockDTO blockDTO) {
        try {
            Block block = Dto2ModelTool.blockDto2Block(blockchainCore.getBlockchainDataBase(),blockDTO);
            blockchainCore.addBlock(block);
            return API.Response.OK;
        } catch (Exception e){
            String message = "commit block failed";
            logger.error(message,e);
            return API.Response.ERROR;
        }
    }
}