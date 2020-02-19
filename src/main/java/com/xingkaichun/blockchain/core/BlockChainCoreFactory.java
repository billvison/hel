package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.impl.*;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;

public class BlockChainCoreFactory {


    public BlockChainCore createBlockChainCore() throws Exception {
        String dbPath = "D:\\logs\\hellowordblockchain\\" ;
        String minerPublicKeyString = "minerPublicKey" ;

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensus();
        BlockChainDataBase blockChainDataBase = new BlockChainDataBaseDefaultImpl(dbPath+"BlockChainDataBase",incentive,consensus);

        TransactionDataBase transactionDataBase = new TransactionDataBaseDefaultImpl();

        MinerTransactionDataBase minerTransactionDataBase = new MinerTransactionDataBaseDefaultImpl(dbPath+"MinerTransactionDataBase",transactionDataBase);
        PublicKeyString minerPublicKey = new PublicKeyString(minerPublicKeyString);
        Miner miner = new MinerDefaultImpl(blockChainDataBase, minerTransactionDataBase,minerPublicKey);

        SynchronizerDataBase synchronizerDataBase = new SynchronizerDataBaseDefaultImpl(dbPath+"Synchronizer","otherNodeBlock.data",transactionDataBase);
        BlockChainDataBase blockChainDataBaseDuplicate = new BlockChainDataBaseDefaultImpl(dbPath+"BlockChainDataBaseDuplicate",incentive,consensus);
        Synchronizer synchronizer = new SynchronizerDefaultImpl(blockChainDataBase,blockChainDataBaseDuplicate, synchronizerDataBase);

        BlockChainCore blockChainCore = new BlockChainCore(miner,synchronizer);
        return blockChainCore;
    }
}
