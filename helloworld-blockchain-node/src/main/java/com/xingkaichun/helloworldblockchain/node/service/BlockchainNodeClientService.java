package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.dto.node.Node;

public interface BlockchainNodeClientService {

    /**
     * 提交交易至其它节点
     */
    boolean sumiteTransaction(Node node, TransactionDTO transactionDTO) throws Exception ;
}
