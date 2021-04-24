package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;

/**
 * 矿工挖矿激励
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class Incentive {

    /**
     * 奖励给矿工的挖矿激励金额。
     */
    public abstract long incentiveAmount(Block block) ;
}
