package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

public interface BlockchainNodeServerService {

    /**
     * 接收交易
     */
    void receiveTransaction(TransactionDTO transactionDTO) throws Exception ;
}
