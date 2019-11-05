package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.model.Block;

/**
 * 挖矿难度
 */
public class MineDifficulty {
    /**
     * 挖矿的难度
     * @param blockChainCore 区块链
     * @param block 目标区块的难度
     */
    public int difficulty(BlockChainCore blockChainCore, Block block){
        //TODO 不同版本挖矿难度可能不同
        return 1;
    }
}
