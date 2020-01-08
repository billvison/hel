package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainSynchronizer;
import com.xingkaichun.blockchain.core.Coordinator;
import com.xingkaichun.blockchain.core.Miner;

public class CoordinatorDefaultImpl implements Coordinator {

    private Miner miner;
    private BlockChainSynchronizer blockChainSynchronizer;

    @Override
    public void run() throws Exception {
        while (isActive()){
            blockChainSynchronizer.synchronizeBlockChainNode();
            miner.mine();
        }
    }

    @Override
    public void pause() throws Exception {
        blockChainSynchronizer.pauseSynchronizeBlockChainNode();
        miner.pauseMine();
    }

    @Override
    public void resume() throws Exception {
        blockChainSynchronizer.resumeSynchronizeBlockChainNode();
        miner.resumeMine();
    }

    public boolean isActive() throws Exception {
        return blockChainSynchronizer.isActive() || miner.isActive();
    }
}
