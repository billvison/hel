package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

/**
 * 工作量证明
 */
public class ProofOfWorkConsensus implements Consensus{

    @Override
    public boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block) {
        return false;
    }

    @Override
    public String difficulty(BlockChainDataBase blockChainDataBase, Block block){
        return "0000";
    }
}
