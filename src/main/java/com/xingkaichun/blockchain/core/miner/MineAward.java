package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.model.Block;

import java.math.BigDecimal;

/**
 * 挖矿难度
 */
public class MineAward {
    /**
     * 挖矿的奖励
     * @param blockChainCore 区块链
     * @param block 目标区块的难度
     */
    public BigDecimal difficulty(BlockChainCore blockChainCore, Block block){
        return new BigDecimal("100");
    }
}
