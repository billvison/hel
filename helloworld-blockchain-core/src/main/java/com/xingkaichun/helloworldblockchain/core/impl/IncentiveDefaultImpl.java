package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public long incentiveAmount(Block block) {
        //交易手续费
        long fee = BlockTool.getBlockFee(block);
        //系统给予的挖矿津贴
        long subsidy = getSubsidy(block);
        //总的挖矿奖励
        long total = subsidy + fee;
        return total;
    }

    /**
     * 系统给予的挖矿津贴
     */
    private long getSubsidy(Block block) {
        return 100000000;
    }


}
