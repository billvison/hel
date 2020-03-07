package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class IncentiveDefaultImpl extends Incentive {

    private Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) throws Exception {

        BigDecimal initCoin = new BigDecimal("100");

        int blockHeight = block.getHeight();
        if(blockHeight <= 1){
            return initCoin;
        }

        Block firstBlock = blockChainDataBase.findBlockByBlockHeight(1);
        //递减周期
        long timestamp = 1 * 24 * 60 * 60 * 1000;
        long totalTimestamp = System.currentTimeMillis() - firstBlock.getTimestamp();
        long multiple = totalTimestamp / timestamp;
        while (multiple > 1){
            initCoin = initCoin.divide(new BigDecimal("2"));
            --multiple;
        }
        return initCoin;
    }
}
