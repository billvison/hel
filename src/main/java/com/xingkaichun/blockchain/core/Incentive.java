package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

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
    BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) ;
}
