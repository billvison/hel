package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.MineDifficulty;
import com.xingkaichun.blockchain.core.model.Block;

public class MineDifficultyDefaultImpl implements MineDifficulty {

    public String difficulty(BlockChainDataBase blockChainDataBase, Block block){
        return "0000";
    }
}
