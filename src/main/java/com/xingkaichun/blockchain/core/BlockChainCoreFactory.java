package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.impl.*;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;

public class BlockChainCoreFactory {


    public BlockChainCore createBlockChainCore() throws Exception {

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensus();
        BlockChainDataBase blockChainDataBase = new BlockChainDataBaseDefaultImpl("",incentive,consensus);

        ForMinerTransactionDataBase forMinerTransactionDataBase = new ForMinerTransactionDataBaseDefaultImpl("");
        PublicKeyString minerPublicKey = new PublicKeyString("");
        Miner miner = new MinerDefaultImpl(blockChainDataBase,forMinerTransactionDataBase,minerPublicKey);

        ForSynchronizerDataBase forSynchronizerDataBase = new ForSynchronizerDataBaseDefaultImpl("");
        BlockChainDataBase blockChainDataBaseDuplicate = new BlockChainDataBaseDefaultImpl("",incentive,consensus);
        Synchronizer synchronizer = new SynchronizerDefaultImpl(blockChainDataBase,blockChainDataBaseDuplicate,forSynchronizerDataBase);

        BlockChainCore blockChainCore = new BlockChainCore(miner,synchronizer);
        return blockChainCore;
    }
}
