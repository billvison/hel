package com.xingkaichun.helloworldblockchain.netcore.server;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点请求处理器
 *
 * @author 邢开春 409060350@qq.com
 */
public class HttpServerHandlerResolver {

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
     * @return
     */
    public PingResponse ping(String requestIp, PingRequest request){
        try {
            //将ping的来路作为区块链节点
            if(configurationService.isAutoSearchNode()){
                NodeEntity node = new NodeEntity();
                node.setIp(requestIp);
                nodeService.addNode(node);
                LogUtil.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",requestIp,GlobalSetting.DEFAULT_PORT));
            }
            PingResponse response = new PingResponse();
            return response;
        } catch (Exception e){
            String message = "ping node failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 根据区块高度查询区块
     */
    public GetBlockResponse getBlock(GetBlockRequest request){
        try {
            Block blockByBlockHeight = blockchainCore.queryBlockByBlockHeight(request.getHeight());
            BlockDTO block = Model2DtoTool.block2BlockDTO(blockByBlockHeight);
            GetBlockResponse response = new GetBlockResponse();
            response.setBlock(block);
            return response;
        } catch (Exception e){
            String message = "get block failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    /**
     * 接收其它节点提交的交易
     */
    public PostTransactionResponse postTransaction(PostTransactionRequest request){
        try {
            blockchainCore.submitTransaction(request.getTransaction());
            PostTransactionResponse response = new PostTransactionResponse();
            return response;
        } catch (Exception e){
            String message = "post transaction failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public PostBlockResponse postBlock(PostBlockRequest request) {
        try {
            Block block = Dto2ModelTool.blockDto2Block(blockchainCore.getBlockchainDataBase(),request.getBlock());
            blockchainCore.addBlock(block);
            PostBlockResponse response = new PostBlockResponse();
            return response;
        } catch (Exception e){
            String message = "post block failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public GetNodesResponse getNodes(GetNodesRequest request) {
        try {
            List<NodeEntity> nodeList = nodeService.queryAllNodeList();
            if(nodeList == null){
                nodeList = new ArrayList<>();
            }
            String[] nodes = new String[nodeList.size()];
            for (int i=0;i<nodeList.size();i++){
                NodeEntity nodeEntity = nodeList.get(i);
                nodes[i] = nodeEntity.getIp();
            }
            GetNodesResponse response = new GetNodesResponse();
            response.setNodes(nodes);
            return response;
        }catch (Exception e){
            String message = "get nodes failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public PostBlockchianHeightResponse postBlockchainHeight(String requestIp, PostBlockchianHeightRequest request) {
        try {
            NodeEntity node = new NodeEntity();
            node.setIp(requestIp);
            node.setBlockchainHeight(request.getHeight());
            nodeService.updateNode(node);

            PostBlockchianHeightResponse response = new PostBlockchianHeightResponse();
            return response;
        } catch (Exception e){
            String message = "post blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public GetBlockchianHeightResponse getBlockchainHeight(GetBlockchianHeightRequest request) {
        try {
            long blockchainHeight = blockchainCore.queryBlockchainHeight();
            GetBlockchianHeightResponse response = new GetBlockchianHeightResponse();
            response.setBlockchainHeight(blockchainHeight);
            return response;
        } catch (Exception e){
            String message = "get blockchain height failed";
            LogUtil.error(message,e);
            return null;
        }
    }

    public GetTransactionResponse getTransaction(GetTransactionRequest request) {
        try {
            Transaction transactionByTransactionHeight = blockchainCore.queryTransactionByTransactionHeight(request.getHeight());
            TransactionDTO transactionDTO = Model2DtoTool.transaction2TransactionDTO(transactionByTransactionHeight);
            GetTransactionResponse response = new GetTransactionResponse();
            response.setTransaction(transactionDTO);
            return response;
        } catch (Exception e){
            String message = "get transaction failed";
            LogUtil.error(message,e);
            return null;
        }
    }
}