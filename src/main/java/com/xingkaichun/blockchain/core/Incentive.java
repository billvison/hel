package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.math.BigDecimal;

/**
 * 挖矿激励
 */
public interface Incentive {

    /**
     * 挖矿的奖励
     * @param blockChainDataBase 区块链
     * @param block 待挖矿的区块
     * @return
     */
    //TODO 奖励应当是一笔交易 可以兼容
    BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) ;

    /**
     * 生成激励交易
     * @param blockChainDataBase 区块链
     * @param block 需要产生激励交易的区块
     */
    Transaction incentiveTransaction(BlockChainDataBase blockChainDataBase, Block block);
}
