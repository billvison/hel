package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

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
