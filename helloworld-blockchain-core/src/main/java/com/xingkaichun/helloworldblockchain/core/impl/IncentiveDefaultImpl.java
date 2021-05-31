package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    @Override
    public long incentiveAmount(BlockchainDatabase blockchainDataBase, Block block) {
        //给予矿工的挖矿津贴
        long minerSubsidy = getMinerSubsidy(block);
        //给予矿工的交易手续费
        long minerFee = BlockTool.getBlockFee(block);
        //总的激励
        return minerSubsidy + minerFee;
    }

    @Override
    public boolean isIncentiveRight(BlockchainDatabase blockchainDataBase, Block block) {
        long writeIncentiveValue = BlockTool.getMinerIncentiveValue(block);
        long targetIncentiveValue = incentiveAmount(blockchainDataBase,block);
        if(writeIncentiveValue != targetIncentiveValue){
            LogUtil.debug("区块数据异常，挖矿奖励数据异常。");
            return false;
        }
        return true;
    }

    private static long getMinerSubsidy(Block block) {
        long initCoin = 5000000000L;
        long multiple = (block.getHeight() - 1L) / 210000L;
        while (multiple > 0){
            initCoin = initCoin / 2L;
            --multiple;
        }
        return initCoin;
    }
}