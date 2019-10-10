package com.xingkaichun.blockchain.core;


import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

/**
 * 区块校验者
 */
public interface Checker {
    /**
     * 检测区块
     */
    boolean checkBlockOfNextAddToBlockChain(BlockChainCore blockChainCore, Block block) throws Exception;

    /**
     * 校验交易的合法性
     */
    boolean checkTransaction(BlockChainCore blockChainCore, Transaction transaction) throws Exception;

}
