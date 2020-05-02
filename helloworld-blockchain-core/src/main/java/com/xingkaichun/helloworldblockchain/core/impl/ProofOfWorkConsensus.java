package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.ConsensusVariableHolder;
import com.xingkaichun.helloworldblockchain.core.utils.BlockUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;

/**
 * 工作量证明实现
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ProofOfWorkConsensus extends Consensus {

    private final static String EXPLAIN = "explain";
    private final static String TARGET_DIFFICULT = "targetDifficult";

    @Override
    public boolean isReachConsensus(BlockChainDataBase blockChainDataBase,Block block) {
        ConsensusVariableHolder consensusVariableHolder = block.getConsensusVariableHolder();
        if(consensusVariableHolder == null){
            consensusVariableHolder = calculateConsensusVariableHolder(blockChainDataBase,block);
            block.setConsensusVariableHolder(consensusVariableHolder);
        }
        String targetDifficult = consensusVariableHolder.get(TARGET_DIFFICULT);
        //区块Hash
        String hash = block.getHash();
        if(hash == null){
            hash = BlockUtils.calculateBlockHash(block);
        }
        return hash.startsWith(targetDifficult);
    }


    public ConsensusVariableHolder calculateConsensusVariableHolder(BlockChainDataBase blockChainDataBase, Block block) {

        ConsensusVariableHolder consensusVariableHolder = new ConsensusVariableHolder();
        block.setConsensusVariableHolder(consensusVariableHolder);

        //目标难度
        final String targetDifficult = BlockChainCoreConstants.INIT_GENERATE_BLOCK_DIFFICULTY_STRING;
        consensusVariableHolder.put(EXPLAIN,targetDifficult);
        consensusVariableHolder.put(TARGET_DIFFICULT,targetDifficult);
        return consensusVariableHolder;
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
