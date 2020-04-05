package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.ConsensusTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作量证明实现
 */
public class ProofOfWorkConsensus extends Consensus {

    private Logger logger = LoggerFactory.getLogger(ProofOfWorkConsensus.class);

    @Override
    public boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        //区块中写入的区块Hash
        String hash = block.getHash();
        //挖矿难度
        String stringConsensusTarget = block.getConsensusTarget().getValue();
        return hash.startsWith(stringConsensusTarget);
    }

    public ConsensusTarget calculateConsensusTarget(BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        ConsensusTarget consensusTarget = new ConsensusTarget();
        consensusTarget.setValue(BlockChainCoreConstants.INIT_GENERATE_BLOCK_DIFFICULTY_STRING);
        return consensusTarget;
/*
        int blockHeight = block.getHeight();
        if(blockHeight <= 2){
            consensusTarget.setValue(BlockChainCoreConstants.INIT_GENERATE_BLOCK_DIFFICULTY_STRING);
            return consensusTarget;
        }
        Block previousBlock = blockChainDataBase.findBlockByBlockHeight(blockHeight-1);
        Block previousPreviousBlock = blockChainDataBase.findBlockByBlockHeight(blockHeight-2);

        long previousBlockTimestamp = previousBlock.getTimestamp();
        long previousPreviousBlockTimestamp = previousPreviousBlock.getTimestamp();
        long blockIntervalTimestamp = previousBlockTimestamp - previousPreviousBlockTimestamp;

        //允许产生区块时间的波动范围
        long minTargetTimestamp = BlockChainCoreConstants.GENERATE_BLOCK_AVERAGE_TIMESTAMP / 4;
        long maxTargetTimestamp = BlockChainCoreConstants.GENERATE_BLOCK_AVERAGE_TIMESTAMP * 4;

        String stringConsensusTarget = previousBlock.getConsensusTarget().getValue();
        if(blockIntervalTimestamp < minTargetTimestamp){
            stringConsensusTarget = stringConsensusTarget + "0";
            consensusTarget.setValue(stringConsensusTarget);
            return consensusTarget;
        } else if(blockIntervalTimestamp > maxTargetTimestamp){
            stringConsensusTarget = stringConsensusTarget.substring(0,stringConsensusTarget.length()-1);
            consensusTarget.setValue(stringConsensusTarget);
            return consensusTarget;
        } else {
            consensusTarget.setValue(stringConsensusTarget);
            return consensusTarget;
        }
*/
    }
}
