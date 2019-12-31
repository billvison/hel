package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.miner.MineAward;
import com.xingkaichun.blockchain.core.miner.MineDifficulty;
import com.xingkaichun.blockchain.core.miner.Miner;
import com.xingkaichun.blockchain.core.miner.ForMinerTransactionDataBase;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;

public class BlockChainCore {

    private BlockChainDataBase blockChainDataBase;
    private Miner miner;
    private ForMinerTransactionDataBase forMinerTransactionDataBase;

    public BlockChainCore(ForMinerTransactionDataBase forMinerTransactionDataBase, MineDifficulty mineDifficulty, MineAward mineAward, PublicKeyString minerPublicKey) {


    }
}