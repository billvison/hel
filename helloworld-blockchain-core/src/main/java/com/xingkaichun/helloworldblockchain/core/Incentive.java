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
     * 矿工挖矿成功后，奖励给矿工的激励金额。
     */
    public abstract long incentiveValue(BlockchainDatabase blockchainDataBase, Block block) ;

    /**
     * 校验激励。
     */
    public abstract boolean checkIncentive(BlockchainDatabase blockchainDataBase, Block block) ;
}
