package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.model.Block;

/**
 * 挖矿难度
 */
public class MineDifficulty {
    /**
     * 挖矿的难度
     * @param blockChainDataBase 区块链
     * @param block 目标区块
     */
    public int difficulty(BlockChainDataBase blockChainDataBase, Block block){
        return 4;
    }
}
