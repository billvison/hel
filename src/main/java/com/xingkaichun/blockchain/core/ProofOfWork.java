package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

/**
 * 工作量证明
 */
public class ProofOfWork implements Consensus{

    @Override
    public boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block) {
        return false;
    }
}