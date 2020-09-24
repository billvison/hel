package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;

import java.math.BigDecimal;

/**
 * 矿工挖矿激励
 * 相关拓展：bitcoin总数为什么是2100万个？https://zhuanlan.zhihu.com/p/258953345
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public abstract class Incentive {

    /**
     * 奖励给矿工的挖矿激励数额。
     * 这里只给出挖矿的激励数额。至于这个激励怎么分配，应当由矿工进行决定。
     */
    public abstract BigDecimal mineAward(Block block) ;
}
