package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.model.Block;

import java.math.BigDecimal;

/**
 * 挖矿激励
 */
public abstract class Incentive {

    /**
     * 最多奖励给矿工的激励数额。
     * 这里只给出挖矿的最大激励。至于这个激励怎么分配，应当由矿工自己抉择。
     * 矿工获取的到的挖矿激励不应当大于这个值。
     * @param blockChainDataBase 区块链
     * @param block 待挖矿的区块
     */
    public abstract BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) ;
}
