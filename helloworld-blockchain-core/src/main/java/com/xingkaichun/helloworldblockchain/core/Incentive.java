package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;

import java.math.BigDecimal;

/**
 * 矿工挖矿激励
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public abstract class Incentive {

    /**
     * 奖励给矿工的激励数额。
     * 这里只给出挖矿的激励数额。至于这个激励怎么分配，应当由矿工进行决定。
     * 矿工获取的挖矿激励数额不应当大于这个值。
     * @param blockChainDataBase 区块链
     * @param block 待挖矿的区块
     */
    public abstract BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) throws Exception;
}
