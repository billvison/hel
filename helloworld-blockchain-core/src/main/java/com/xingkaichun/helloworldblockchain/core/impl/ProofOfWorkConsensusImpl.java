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

    /**
     * 计算生成区块的难度
     * @param blockchainDataBase
     * @param block
     * @return
     */
    public String calculateDifficult(BlockchainDatabase blockchainDataBase, Block block) {
        // 一个难度周期区块数量
        long intervalBlockCount = GlobalSetting.IncentiveConstant.INTERVAL_BLOCK;

        String targetDifficult;
        // 新区块高度
        long blockHeight = block.getHeight();
        if(blockHeight == 1){
            // 创世区块使用默认难度值
            targetDifficult = GlobalSetting.GenesisBlock.DIFFICULTY;
            return targetDifficult;
        }
        // 链上最后一个区块
        Block lastBlock = blockchainDataBase.queryBlockByBlockHeight(blockHeight-1);
        long lastBlockHeight = lastBlock.getHeight();
        // 一个周期未结束时，难度不变
        if (lastBlockHeight % intervalBlockCount != 0){
            targetDifficult = lastBlock.getDifficulty();
            return targetDifficult;
        }
        // *** 计算新周期难度值 ***
        // 此时，最后一个区块是上一个周期的最后一个区块。
        Block intervalLastBlock = lastBlock;
        // 上个周期第一个区块
        Block intervalFirstBlock = blockchainDataBase.queryBlockByBlockHeight(lastBlockHeight-intervalBlockCount+1);
        // 上个周期出块总时长
        long actualTimespan = intervalLastBlock.getTimestamp() - intervalFirstBlock.getTimestamp();
        // 限制难度上下限 为（期望值/4 至 期望值*4）之间
        if (actualTimespan < GlobalSetting.IncentiveConstant.INTERVAL_TIME /4){
            actualTimespan = GlobalSetting.IncentiveConstant.INTERVAL_TIME /4;
        }
        if (actualTimespan > GlobalSetting.IncentiveConstant.INTERVAL_TIME *4){
            actualTimespan = GlobalSetting.IncentiveConstant.INTERVAL_TIME *4;
        }

        BigInteger bigIntegerTargetDifficult =
                new BigInteger(intervalLastBlock.getDifficulty(),16)
                        .multiply(new BigInteger(String.valueOf(actualTimespan)))
                        .divide(new BigInteger(String.valueOf(GlobalSetting.IncentiveConstant.INTERVAL_TIME)));
        return bigIntegerTargetDifficult.toString(16);
    }
}
