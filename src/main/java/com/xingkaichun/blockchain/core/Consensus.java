package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

/**
 * 共识
 */
public interface Consensus {

    boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block);
}
