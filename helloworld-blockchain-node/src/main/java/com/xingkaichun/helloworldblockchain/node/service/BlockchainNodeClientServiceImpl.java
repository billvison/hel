package com.xingkaichun.helloworldblockchain.node.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceCode;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.node.Node;
import com.xingkaichun.helloworldblockchain.node.dto.node.NodeApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.ReceiveTransactionRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.response.ReceiveTransactionResponse;
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
    public boolean sumiteTransaction(Node node, TransactionDTO transactionDTO) throws Exception {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),node.getPort(), NodeApiRoute.RECEIVE_TRANSACTION);
            ReceiveTransactionRequest request = new ReceiveTransactionRequest();
            request.setTransactionDTO(transactionDTO);
            String html = NetUtil.getHtml(url,request);
            Type jsonType = new TypeToken<ServiceResult<ReceiveTransactionResponse>>() {}.getType();
            ServiceResult<ReceiveTransactionResponse> pingResponseServiceResult = gson.fromJson(html,jsonType);
            return pingResponseServiceResult != null && pingResponseServiceResult.getServiceCode() == ServiceCode.SUCCESS;
        } catch (IOException e) {
            logger.info(String.format("节点%s:%d网络异常",node.getIp(),node.getPort()),e);
            return false;
        }
    }
}
