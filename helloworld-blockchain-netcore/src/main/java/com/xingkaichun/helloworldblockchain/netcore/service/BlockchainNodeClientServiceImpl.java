package com.xingkaichun.helloworldblockchain.netcore.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeServerApiRoute;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.SimpleNodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request.*;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.*;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainNodeClientServiceImpl implements BlockchainNodeClientService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainNodeClientServiceImpl.class);

    private Gson gson;
    private int serverPort;

    public BlockchainNodeClientServiceImpl(int serverPort) {
        this.serverPort = serverPort;
        this.gson = new Gson();
    }

    @Override
    public ServiceResult<EmptyResponse> sumiteTransaction(SimpleNodeDto node, TransactionDTO transactionDTO) throws Exception {
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
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.debug(String.format("提交交易[%s]至节点[%s:%d]出现异常",gson.toJson(transactionDTO),node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    public ServiceResult<PingResponse> pingNode(SimpleNodeDto node) {
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
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.debug(String.format("Ping节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    public ServiceResult<EmptyResponse> unicastLocalBlockChainHeight(SimpleNodeDto node, BigInteger localBlockChainHeight) {
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
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.debug(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    @Override
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(NodeDto node, BigInteger blockHeight) {
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
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.debug(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }

    @Override
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(NodeDto node, BigInteger blockHeight) {
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
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        } catch (Exception e) {
            logger.debug(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),node.getPort()),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()));
        }
    }
}