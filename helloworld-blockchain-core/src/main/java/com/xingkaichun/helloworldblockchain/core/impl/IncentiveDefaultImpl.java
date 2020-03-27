package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.helloworldblockchain.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class IncentiveDefaultImpl extends Incentive {

    private Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) throws Exception {

        BigDecimal initCoin = BlockChainCoreConstants.INIT_MINE_BLOCK_INCENTIVE_COIN_AMOUNT;

        BigInteger blockHeight = block.getHeight();
        if(BigIntegerUtil.isLessEqualThan(blockHeight,BigInteger.valueOf(1))){
            return initCoin;
        }

        Block firstBlock = blockChainDataBase.findBlockByBlockHeight(BigInteger.valueOf(1));
        long timestamp = BlockChainCoreConstants.MINE_BLOCK_INCENTIVE_REDUCE_BY_HALF_INTERVAL_TIMESTAMP;
        long totalTimestamp = System.currentTimeMillis() - firstBlock.getTimestamp();
        long multiple = totalTimestamp / timestamp;
        while (multiple > 1){
            initCoin = initCoin.divide(new BigDecimal("2"));
            --multiple;
        }
        return initCoin;
    }
}
