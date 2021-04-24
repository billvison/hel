package com.xingkaichun.helloworldblockchain.netcore.server;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.API;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
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
    public String ping(ChannelHandlerContext ctx){
        try {
            //将ping的来路作为区块链节点
            if(configurationService.isAutoSearchNode()){
                NodeEntity node = new NodeEntity();
                InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = inetSocketAddress.getAddress().getHostAddress();
                node.setIp(ip);
                nodeService.addNode(node);
                logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,GlobalSetting.DEFAULT_PORT));
            }
            return API.Response.OK;
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
            return JsonUtil.toJson(block);
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

    public String getNodes() {
        List<NodeEntity> nodeList = nodeService.queryAllNodeList();
        if(nodeList == null){
            nodeList = new ArrayList<>();
        }
        String[] nodes = new String[nodeList.size()];
        for (int i=0;i<nodeList.size();i++){
            NodeEntity nodeEntity = nodeList.get(i);
            nodes[i] = nodeEntity.getIp();
        }
        return JsonUtil.toJson(nodes);
    }

    public String postBlockchainHeight(ChannelHandlerContext ctx, long height) {
        try {
            NodeEntity node = new NodeEntity();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = inetSocketAddress.getAddress().getHostAddress();
            node.setIp(ip);
            node.setBlockchainHeight(height);
            nodeService.updateNode(node);
            return API.Response.OK;
        } catch (Exception e){
            String message = "post block height failed";
            logger.error(message,e);
            return API.Response.ERROR;
        }
    }

    public String getBlockchainHeight() {
        return Long.toString(blockchainCore.queryBlockchainHeight());
    }

    public String getTransaction(long height) {
        try {
            Transaction transactionByTransactionHeight = blockchainCore.queryTransactionByTransactionHeight(height);
            TransactionDTO transaction = Model2DtoTool.transaction2TransactionDTO(transactionByTransactionHeight);
            return JsonUtil.toJson(transaction);
        } catch (Exception e){
            String message = "query transaction by transaction height failed";
            logger.error(message,e);
            return API.Response.ERROR;
        }
    }
}