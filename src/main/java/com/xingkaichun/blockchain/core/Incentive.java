package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

import java.math.BigDecimal;

/**
 * 挖矿激励
 */
public interface Incentive {

    /**
     * 挖矿者的最大挖矿激励，挖矿者获取的到的挖矿激励应当不大于这个值。
     * 这里只给出挖矿的最大激励。至于这个激励怎么分配，应当由矿工自己抉择。
     * @param blockChainDataBase 区块链
     * @param block 待挖矿的区块
     * @return
     */
    BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) ;
}
