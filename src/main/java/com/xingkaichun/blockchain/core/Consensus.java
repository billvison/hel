package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

/**
 * 共识
 */
public interface Consensus {

    /**
     * 这个区块写入的nonce达成共识了吗？
     *
     * @param blockChainDataBase 区块链
     * @param block              需要被验证nonce是否已经达成了共识的区块
     */
    boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block);
}

