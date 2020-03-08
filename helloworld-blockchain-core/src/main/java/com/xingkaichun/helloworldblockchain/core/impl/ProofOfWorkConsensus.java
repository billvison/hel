package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.helloworldblockchain.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作量证明
 */
public class ProofOfWorkConsensus extends Consensus {

    private Logger logger = LoggerFactory.getLogger(ProofOfWorkConsensus.class);

    @Override
    public boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        //区块中写入的区块Hash
        String hash = block.getHash();
        //挖矿难度
        String difficulty = difficulty(blockChainDataBase,block);
        return isHashRight(difficulty,hash);
    }

    public String difficulty(BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        int blockHeight = block.getHeight();
        if(blockHeight <= 2){
            return BlockChainCoreConstants.INIT_GENERATE_BLOCK_DIFFICULTY_STRING;
        }
        Block previousBlock = blockChainDataBase.findBlockByBlockHeight(blockHeight-1);
        Block previousPreviousBlock = blockChainDataBase.findBlockByBlockHeight(blockHeight-2);

        long previousBlockTimestamp = previousBlock.getTimestamp();
        long previousPreviousBlockTimestamp = previousPreviousBlock.getTimestamp();
        long blockIntervalTimestamp = previousBlockTimestamp - previousPreviousBlockTimestamp;

        //允许产生区块时间的波动范围
        long minTargetTimestamp = BlockChainCoreConstants.GENERATE_BLOCK_AVERAGE_TIMESTAMP / 4;
        long maxTargetTimestamp = BlockChainCoreConstants.GENERATE_BLOCK_AVERAGE_TIMESTAMP * 4;

        String difficultyString = previousBlock.getDifficultyString();
        if(blockIntervalTimestamp < minTargetTimestamp){
            return difficultyString + "0";
        } else if(blockIntervalTimestamp > maxTargetTimestamp){
            return difficultyString.substring(0,difficultyString.length()-1);
        } else {
            return difficultyString;
        }
    }

    /**
     * Hash满足挖矿难度的要求吗？
     * @param targetDificulty 目标挖矿难度
     * @param hash 需要校验的Hash
     */
    private boolean isHashRight(String targetDificulty, String hash){
        return hash.startsWith(targetDificulty);
    }
}
