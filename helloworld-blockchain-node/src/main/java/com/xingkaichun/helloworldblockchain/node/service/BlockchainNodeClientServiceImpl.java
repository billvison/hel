package com.xingkaichun.helloworldblockchain.node.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.*;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.NodeServerApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.node.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Service
public class BlockchainNodeClientServiceImpl implements BlockchainNodeClientService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainNodeClientServiceImpl.class);

    @Autowired
    private BlockChainCore blockChainCore;

    @Autowired
    private Gson gson;

    @Value("${server.port}")
    private int serverPort;

    @Override
    public ServiceResult<EmptyResponse> sumiteTransaction(SimpleNode node, TransactionDTO transactionDTO) throws Exception {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeServerApiRoute.RECEIVE_TRANSACTION);
            ReceiveTransactionRequest request = new ReceiveTransactionRequest();
            request.setTransactionDTO(transactionDTO);
            String html = NetUtil.getHtml(url,request);
            Type jsonType = new TypeToken<ServiceResult<ReceiveTransactionResponse>>() {}.getType();
            ServiceResult<ReceiveTransactionResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                return ServiceResult.createSuccessServiceResult("");
            } else {
                return ServiceResult.createFailServiceResult(pingResponseServiceResult.getMessage());
            }
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.error(String.format("提交交易[%s]至节点[%s:%d]出现异常",gson.toJson(transactionDTO),node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    public ServiceResult<PingResponse> pingNode(SimpleNode node) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeServerApiRoute.PING);
            PingRequest pingRequest = new PingRequest();
            pingRequest.setPort(serverPort);
            String html = NetUtil.getHtml(url,pingRequest);
            Type jsonType = new TypeToken<ServiceResult<PingResponse>>() {}.getType();
            ServiceResult<PingResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                return pingResponseServiceResult;
            } else {
                return ServiceResult.createFailServiceResult(pingResponseServiceResult.getMessage());
            }
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.error(String.format("Ping节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    public ServiceResult<EmptyResponse> unicastLocalBlockChainHeight(SimpleNode node, BigInteger localBlockChainHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(), node.getPort(), NodeServerApiRoute.ADD_OR_UPDATE_NODE);
            AddOrUpdateNodeRequest request = new AddOrUpdateNodeRequest();
            request.setPort(serverPort);
            request.setBlockChainHeight(localBlockChainHeight);
            String html = NetUtil.getHtml(url,request);
            Type jsonType = new TypeToken<ServiceResult<AddOrUpdateNodeResponse>>() {}.getType();
            ServiceResult<AddOrUpdateNodeResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                return ServiceResult.createSuccessServiceResult("");
            } else {
                return ServiceResult.createFailServiceResult(pingResponseServiceResult.getMessage());
            }
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.error(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    @Override
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(Node node, BigInteger blockHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeServerApiRoute.QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT);
            QueryBlockHashByBlockHeightRequest request = new QueryBlockHashByBlockHeightRequest();
            request.setBlockHeight(blockHeight);
            String html = NetUtil.getHtml(url,request);
            Type jsonType = new TypeToken<ServiceResult<QueryBlockHashByBlockHeightResponse>>() {}.getType();
            ServiceResult<QueryBlockHashByBlockHeightResponse> serviceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(serviceResult)){
                return serviceResult;
            } else {
                return ServiceResult.createFailServiceResult(serviceResult.getMessage());
            }
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.error(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    @Override
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(Node node, BigInteger blockHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeServerApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT);
            QueryBlockDtoByBlockHeightRequest request = new QueryBlockDtoByBlockHeightRequest();
            request.setBlockHeight(blockHeight);
            String html = NetUtil.getHtml(url,request);
            Type jsonType = new TypeToken<ServiceResult<QueryBlockDtoByBlockHeightResponse>>() {}.getType();
            ServiceResult<QueryBlockDtoByBlockHeightResponse> serviceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(serviceResult)){
                return serviceResult;
            } else {
                return ServiceResult.createFailServiceResult(serviceResult.getMessage());
            }
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.error(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }
}