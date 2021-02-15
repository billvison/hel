package com.xingkaichun.helloworldblockchain.netcore.node.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeServerApiRoute;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request.*;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response.*;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainNodeClientImpl implements BlockchainNodeClient {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainNodeClientImpl.class);

    private Gson gson;

    public BlockchainNodeClientImpl() {
        this.gson = new Gson();
    }

    @Override
    public ServiceResult<SubmitTransactionToNodeResponse> submitTransaction(BaseNodeDto node, TransactionDTO transactionDTO) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(), GlobalSetting.DEFAULT_PORT, NodeServerApiRoute.SUBMIT_TRANSACTION_TO_NODE);
            SubmitTransactionToNodeRequest request = new SubmitTransactionToNodeRequest();
            request.setTransactionDTO(transactionDTO);
            String html = NetUtil.jsonGetRequest(url,request);
            Type jsonType = new TypeToken<ServiceResult<SubmitTransactionToNodeResponse>>() {}.getType();
            ServiceResult<SubmitTransactionToNodeResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                return ServiceResult.createSuccessServiceResult("");
            } else {
                return ServiceResult.createFailServiceResult(pingResponseServiceResult.getMessage());
            }
        } catch (IOException e) {
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        } catch (Exception e) {
            logger.debug(String.format("提交交易[%s]至节点[%s:%d]出现异常",gson.toJson(transactionDTO),node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        }
    }

    public ServiceResult<PingResponse> pingNode(BaseNodeDto node) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),GlobalSetting.DEFAULT_PORT, NodeServerApiRoute.PING);
            PingRequest pingRequest = new PingRequest();
            String html = NetUtil.jsonGetRequest(url,pingRequest);
            Type jsonType = new TypeToken<ServiceResult<PingResponse>>() {}.getType();
            ServiceResult<PingResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                return pingResponseServiceResult;
            } else {
                return ServiceResult.createFailServiceResult(pingResponseServiceResult.getMessage());
            }
        } catch (IOException e) {
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        } catch (Exception e) {
            logger.debug(String.format("Ping节点[%s:%d]出现异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        }
    }

    public ServiceResult<AddOrUpdateNodeResponse> unicastLocalBlockchainHeight(BaseNodeDto node, long localBlockchainHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(), GlobalSetting.DEFAULT_PORT, NodeServerApiRoute.ADD_OR_UPDATE_NODE);
            AddOrUpdateNodeRequest request = new AddOrUpdateNodeRequest();
            request.setBlockchainHeight(localBlockchainHeight);
            String html = NetUtil.jsonGetRequest(url,request);
            Type jsonType = new TypeToken<ServiceResult<AddOrUpdateNodeResponse>>() {}.getType();
            ServiceResult<AddOrUpdateNodeResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(pingResponseServiceResult)){
                return ServiceResult.createSuccessServiceResult("");
            } else {
                return ServiceResult.createFailServiceResult(pingResponseServiceResult.getMessage());
            }
        } catch (IOException e) {
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        } catch (Exception e) {
            logger.debug(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        }
    }

    @Override
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(NodeDto node, Long blockHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),GlobalSetting.DEFAULT_PORT, NodeServerApiRoute.QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT);
            QueryBlockHashByBlockHeightRequest request = new QueryBlockHashByBlockHeightRequest();
            request.setBlockHeight(blockHeight);
            String html = NetUtil.jsonGetRequest(url,request);
            Type jsonType = new TypeToken<ServiceResult<QueryBlockHashByBlockHeightResponse>>() {}.getType();
            ServiceResult<QueryBlockHashByBlockHeightResponse> serviceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(serviceResult)){
                return serviceResult;
            } else {
                return ServiceResult.createFailServiceResult(serviceResult.getMessage());
            }
        } catch (IOException e) {
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        } catch (Exception e) {
            logger.debug(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        }
    }

    @Override
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(NodeDto node, Long blockHeight) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),GlobalSetting.DEFAULT_PORT, NodeServerApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT);
            QueryBlockDtoByBlockHeightRequest request = new QueryBlockDtoByBlockHeightRequest();
            request.setBlockHeight(blockHeight);
            String html = NetUtil.jsonGetRequest(url,request);
            Type jsonType = new TypeToken<ServiceResult<QueryBlockDtoByBlockHeightResponse>>() {}.getType();
            ServiceResult<QueryBlockDtoByBlockHeightResponse> serviceResult = gson.fromJson(html,jsonType);
            if(ServiceResult.isSuccess(serviceResult)){
                return serviceResult;
            } else {
                return ServiceResult.createFailServiceResult(serviceResult.getMessage());
            }
        } catch (IOException e) {
            logger.debug(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        } catch (Exception e) {
            logger.debug(String.format("将本地区块链高度单播给节点[%s:%d]出现异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return ServiceResult.createFailServiceResult(String.format("节点%s:%d网络异常",node.getIp(),GlobalSetting.DEFAULT_PORT));
        }
    }
}