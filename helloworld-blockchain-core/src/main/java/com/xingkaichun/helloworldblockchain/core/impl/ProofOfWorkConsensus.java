package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.ConsensusTarget;
import com.xingkaichun.helloworldblockchain.core.utils.BlockUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;

/**
 * 工作量证明实现
 */
public class ProofOfWorkConsensus extends Consensus {

    public ConsensusTarget calculateConsensusTarget(BlockChainDataBase blockChainDataBase, Block block) {
        //目标难度
        final String targetDifficult = BlockChainCoreConstants.INIT_GENERATE_BLOCK_DIFFICULTY_STRING;
        ConsensusTarget consensusTarget = new ConsensusTarget(){
            private Block consensusBlock = block;
            @Override
            public boolean isReachConsensus() {
                //区块Hash
                String hash = consensusBlock.getHash();
                if(hash == null){
                    hash = BlockUtils.calculateBlockHash(consensusBlock);
                }
                return hash.startsWith(targetDifficult);
            }
        };
        consensusTarget.setExplain("挖矿难度是"+targetDifficult);
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
