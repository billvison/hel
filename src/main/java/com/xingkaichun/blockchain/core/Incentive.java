package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

/**
 * 挖矿激励
 */
public interface Incentive {

    /**
     * 生成激励交易
     * @param blockChainDataBase 区块链
     * @param block 需要产生激励交易的区块
     */
    Transaction incentiveTransaction(BlockChainDataBase blockChainDataBase, Block block);
}
