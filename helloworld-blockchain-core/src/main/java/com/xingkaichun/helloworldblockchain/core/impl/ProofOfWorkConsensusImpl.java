package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.math.BigInteger;

/**
 * 工作量证明实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class ProofOfWorkConsensusImpl extends Consensus {

    @Override
    public boolean isReachConsensus(BlockchainDatabase blockchainDataBase, Block block) {
        String difficulty = block.getDifficulty();
        if(StringUtil.isNullOrEmpty(difficulty)){
            difficulty = calculateDifficult(blockchainDataBase,block);
            block.setDifficulty(difficulty);
        }

        //区块Hash
        String hash = block.getHash();
        if(hash == null){
            hash = BlockTool.calculateBlockHash(block);
        }
        return new BigInteger(difficulty,16).compareTo(new BigInteger(hash,16)) > 0;
    }


    public String calculateDifficult(BlockchainDatabase blockchainDataBase, Block block) {
        long intervalBlockCount = GlobalSetting.IncentiveConstant.INTERVAL_TIME / GlobalSetting.IncentiveConstant.BLOCK_TIME;

        String targetDifficult;
        long blockHeight = block.getHeight();
        if(blockHeight == 1){
            targetDifficult = GlobalSetting.GenesisBlock.DIFFICULTY;
            return targetDifficult;
        }
        Block lastBlock = blockchainDataBase.queryBlockByBlockHeight(blockHeight-1);
        long lastBlockHeight = lastBlock.getHeight();
        if (lastBlockHeight % intervalBlockCount != 0){
            targetDifficult = lastBlock.getDifficulty();
            return targetDifficult;
        }
        //此时，最后一个区块是上一个周期的最后一个区块。
        Block intervalLastBlock = lastBlock;
        Block intervalFirstBlock = blockchainDataBase.queryBlockByBlockHeight(lastBlockHeight-intervalBlockCount+1);
        long actualTimespan = intervalLastBlock.getTimestamp() - intervalFirstBlock.getTimestamp();
        if (actualTimespan < GlobalSetting.IncentiveConstant.INTERVAL_TIME /4){
            actualTimespan = GlobalSetting.IncentiveConstant.INTERVAL_TIME /4;
        }
        if (actualTimespan > GlobalSetting.IncentiveConstant.INTERVAL_TIME *4){
            actualTimespan = GlobalSetting.IncentiveConstant.INTERVAL_TIME *4;
        }
        BigInteger bigIntegerTargetDifficult = new BigInteger(intervalLastBlock.getDifficulty(),16).multiply(new BigInteger(String.valueOf(actualTimespan))).divide(new BigInteger(String.valueOf(GlobalSetting.IncentiveConstant.INTERVAL_TIME)));
        return bigIntegerTargetDifficult.toString(16);
    }
}
