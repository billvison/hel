package com.xingkaichun.helloworldblockchain.application.service;

import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkRequest;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.SubmitTransactionToBlockchainNetworkResponse;
import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PostTransactionRequest;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PostTransactionResponse;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
@Service
public class WalletApplicationServiceImpl implements WalletApplicationService {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Autowired
    private BlockchainCore blockchainCore;



    @Override
    public SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request) {
        TransactionDto transactionDTO = request.getTransactionDTO();
        //将交易提交到本地区块链
        blockchainCore.submitTransaction(transactionDTO);
        //提交交易到网络
        List<Node> nodes = blockchainNetCore.getNodeService().queryAllNodeList();
        List<SubmitTransactionToBlockchainNetworkResponse.Node> successSubmitNode = new ArrayList<>();
        List<SubmitTransactionToBlockchainNetworkResponse.Node> failSubmitNode = new ArrayList<>();
        if(nodes != null){
            for(Node node:nodes){
                PostTransactionRequest postTransactionRequest = new PostTransactionRequest();
                postTransactionRequest.setTransaction(transactionDTO);
                PostTransactionResponse postTransactionResponse = new BlockchainNodeClientImpl(node.getIp()).postTransaction(postTransactionRequest);
                if(postTransactionResponse != null){
                    successSubmitNode.add(new SubmitTransactionToBlockchainNetworkResponse.Node(node.getIp()));
                } else {
                    failSubmitNode.add(new SubmitTransactionToBlockchainNetworkResponse.Node(node.getIp()));
                }
            }
        }

        SubmitTransactionToBlockchainNetworkResponse response = new SubmitTransactionToBlockchainNetworkResponse();
        response.setTransactionDTO(transactionDTO);
        response.setSuccessSubmitNode(successSubmitNode);
        response.setFailSubmitNode(failSubmitNode);
        return response;
    }
}
