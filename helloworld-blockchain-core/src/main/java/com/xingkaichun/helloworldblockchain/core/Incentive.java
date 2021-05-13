package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;

/**
 * 矿工挖矿激励
 * 矿工挖到矿了，系统应该给予矿工多少数字货币的奖励？在这里会确定矿工的具体激励金额。
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class Incentive {

    /**
     * 奖励给矿工的挖矿激励金额。
     */
    public abstract long incentiveAmount(BlockchainDatabase blockchainDataBase, Block block) ;

    /**
     * 奖励给矿工的挖矿激励发放地址。
     */
    public abstract String incentiveAddress(BlockchainDatabase blockchainDataBase, Block block) ;

    /**
     * 校验激励。
     */
    public abstract boolean isIncentiveRight(BlockchainDatabase blockchainDataBase, Block block) ;
}
