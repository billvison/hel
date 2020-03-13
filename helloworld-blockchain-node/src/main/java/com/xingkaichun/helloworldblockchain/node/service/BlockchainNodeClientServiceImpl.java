package com.xingkaichun.helloworldblockchain.node.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.NodeServerApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request.AddOrUpdateNodeRequest;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request.PingRequest;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request.ReceiveTransactionRequest;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.AddOrUpdateNodeResponse;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.PingResponse;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.ReceiveTransactionResponse;
import com.xingkaichun.helloworldblockchain.node.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;

@Service
public class BlockchainNodeClientServiceImpl implements BlockchainNodeClientService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainNodeClientServiceImpl.class);

    @Autowired
    private BlockChainCore blockChainCore;

    @Autowired
    private Gson gson;

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
            String html = NetUtil.getHtml(url,new PingRequest());
            Type jsonType = new TypeToken<ServiceResult<PingResponse>>() {}.getType();
            ServiceResult<PingResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                return ServiceResult.createSuccessServiceResult("");
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

    public ServiceResult<EmptyResponse> unicastLocalBlockChainHeight(SimpleNode node, int localBlockChainHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(), node.getPort(), NodeServerApiRoute.ADD_OR_UPDATE_NODE);
            AddOrUpdateNodeRequest request = new AddOrUpdateNodeRequest();
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
}