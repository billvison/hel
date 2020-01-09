package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.impl.*;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;

public class BlockChainCoreFactory {


    public BlockChainCore createBlockChainCore() throws Exception {

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensus();
        BlockChainDataBase blockChainDataBase = new BlockChainDataBaseDefaultImpl("",incentive,consensus);

        TransactionDataBase transactionDataBase = new TransactionDataBaseDefaultImpl();

        MinerTransactionDataBase minerTransactionDataBase = new MinerTransactionDataBaseDefaultImpl("",transactionDataBase);
        PublicKeyString minerPublicKey = new PublicKeyString("");
        Miner miner = new MinerDefaultImpl(blockChainDataBase, minerTransactionDataBase,minerPublicKey);

        SynchronizerDataBase synchronizerDataBase = new SynchronizerDataBaseDefaultImpl("",transactionDataBase);
        BlockChainDataBase blockChainDataBaseDuplicate = new BlockChainDataBaseDefaultImpl("",incentive,consensus);
        Synchronizer synchronizer = new SynchronizerDefaultImpl(blockChainDataBase,blockChainDataBaseDuplicate, synchronizerDataBase);

        BlockChainCore blockChainCore = new BlockChainCore(miner,synchronizer);
        return blockChainCore;
    }
}
