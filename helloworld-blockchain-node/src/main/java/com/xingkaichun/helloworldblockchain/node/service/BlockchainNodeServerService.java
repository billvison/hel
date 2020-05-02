package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

/**
 * 区块链节点服务端service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface BlockchainNodeServerService {

    /**
     * 接收交易
     */
    void receiveTransaction(TransactionDTO transactionDTO) throws Exception ;
}
