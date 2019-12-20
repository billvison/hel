package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;

import java.math.BigDecimal;

/**
 * 挖矿奖励
 */
public class MineAward {
    /**
     * 挖矿的奖励
     * @param blockChainCore 区块链
     * @param block 目标区块
     */
    public BigDecimal mineAward(BlockChainCore blockChainCore, Block block){
        return new BigDecimal("100");
    }
    public boolean isBlockMineAwardRight(BlockChainCore blockChainCore, Block block, BigDecimal mineAwardByPass){
        BigDecimal mineAward = mineAward(blockChainCore,block);
        return mineAward.compareTo(mineAwardByPass) != 0 ;
    }
}
