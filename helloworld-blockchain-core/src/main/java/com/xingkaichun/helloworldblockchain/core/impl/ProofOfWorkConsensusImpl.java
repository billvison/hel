package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * 工作量证明实现
 * 相关拓展：比特币是如何做到平均10分钟出一个区块？https://zhuanlan.zhihu.com/p/258954220
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ProofOfWorkConsensusImpl extends Consensus {

    private static final Logger logger = LoggerFactory.getLogger(ProofOfWorkConsensusImpl.class);

    @Override
    public boolean isReachConsensus(BlockChainDataBase blockChainDataBase,Block block) {
        String bits = block.getBits();
        if(bits == null || bits.isEmpty()){
            bits = calculateDifficult(blockChainDataBase,block);
            block.setBits(bits);
        }

        //区块Hash
        String hash = block.getHash();
        if(hash == null){
            hash = BlockTool.calculateBlockHash(block);
        }
        return new BigInteger(bits,16).compareTo(new BigInteger(hash,16)) > 0;
    }


    public String calculateDifficult(BlockChainDataBase blockChainDataBase, Block block) {

        long targetTimespan = 1000 * 60 * 60 * 24 * 14;
        long targetSpacing = 1000 * 60 * 10;
        long nInterval = targetTimespan / targetSpacing;

        String targetDifficult;
        long blockHeight = block.getHeight();
        if(blockHeight == 1){
            targetDifficult = GlobalSetting.MinerConstant.INIT_GENERATE_BLOCK_DIFFICULTY_STRING;
            return targetDifficult;
        }
        Block lastBlock = blockChainDataBase.queryBlockByBlockHeight(blockHeight-1);
        long lastBlockHeight = lastBlock.getHeight();
        if (lastBlockHeight % nInterval != 0){
            targetDifficult = lastBlock.getBits();
            return targetDifficult;
        }
        Block Block14DayAgo = blockChainDataBase.queryBlockByBlockHeight(lastBlockHeight-nInterval+1);
        long actualTimespan = lastBlock.getTimestamp() - Block14DayAgo.getTimestamp();
        if (actualTimespan < targetTimespan/4)
            actualTimespan = targetTimespan/4;
        if (actualTimespan > targetTimespan*4)
            actualTimespan = targetTimespan*4;

        BigInteger bigIntegerTargetDifficult = new BigInteger(lastBlock.getBits(),16).multiply(new BigInteger(String.valueOf(actualTimespan))).divide(new BigInteger(String.valueOf(targetTimespan)));
        return bigIntegerTargetDifficult.toString(16);
    }
}
