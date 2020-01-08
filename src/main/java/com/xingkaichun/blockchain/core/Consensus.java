package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

/**
 * 共识
 */
public interface Consensus {

    /**
     * 这个区块写入的nonce达成共识了吗？
     * @param blockChainDataBase 区块链
     * @param block 需要被验证nonce是否已经达成了共识的区块
     */
    boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block);

    /**
     * 挖矿的难度。挖矿的难度决定了nonce获取难度。根本上讲，挖矿难度形成共识，然后倒着推算出nonce。
     * @param blockChainDataBase 区块链
     * @param block 目标区块
     */
    String difficulty(BlockChainDataBase blockChainDataBase, Block block);
}
