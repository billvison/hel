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
     * 计算目标区块的挖矿难度
     *
     * 挖矿的难度在一个挖矿周期内是不变的，而每当一个新的挖矿周期开始时，就需要重新计算新的周期的挖矿难度。
     * 每GlobalSetting.IncentiveConstant.INTERVAL_BLOCK_COUNT个区块为一个挖矿周期。
     * 计算第X周期的难度需要参考第(X-1)周期的耗时。
     * 第X周期的难度 = 第(X-1)周期的难度 * 第(X-1)周期的实际挖矿耗时 / 一个周期的期望挖矿耗时。
     * 第一周期、第二周期的难度默认为创世区块的难度。
     *
     * @param blockchainDataBase "目标区块"所在的区块链
     * @param targetBlock 需要计算难度的"目标区块"
     * @return
     */
    public String calculateDifficult(BlockchainDatabase blockchainDataBase, Block targetBlock) {

        // 目标难度
        String targetDifficult;
        // 目标区块高度
        long targetBlockHeight = targetBlock.getHeight();
        // 目标区块高度在第一周期、第二周期内
        if(targetBlockHeight <= GlobalSetting.IncentiveConstant.INTERVAL_BLOCK_COUNT * 2){
            /**
             * 最初，区块链有区块 0(创世区块)，假设每个挖矿周期有二个区块。
             * 现在要挖第一个周期的区块，即要挖区块1，区块2
             * 第一个周期挖矿难度需要参考前一个周期的挖矿难度，因为没有前一个周期，所以第一个周期的区块默认使用创世区块的难度值。
             * 有了难度，相继把第一个周期内的区块挖出来了，此时，区块链有区块 0(创世区块)，1(高度为1的区块)，2(高度为2的区块)。
             * 现在要计算第二个周期的难度了，此时需要第一个周期的耗时，即[产生区块2的时间戳]减去[开始挖区块1的时刻]
             * ，因为[开始挖区块1的时刻]也不知道(我们知道的是区块1被挖出来的那个时刻，不要搞混)，所以第二个周期的区块也默认使用创世区块的难度值。
             * 有了难度，相继把第二个周期内的区块挖出来了，此时，区块链有区块 0，1，2，3，4。
             * 现在要计算第三个周期的难度了，此时需要第二个周期的耗时，即[产生区块4的时间戳]减去[开始挖区块3的时刻]
             * ，因为[开始挖区块3的时刻]就是[产生区块2的时间戳]，所以第二周期的耗时可以计算。
             * 第二周期耗时=[第二周期最后一个区块的时间戳]减去[第一周期最后一个区块的时间戳]
             */
            targetDifficult = GlobalSetting.GenesisBlock.DIFFICULTY;
            return targetDifficult;
        }

        // 目标区块的上一个区块
        Block targetBlockPreviousBlock = blockchainDataBase.queryBlockByBlockHeight(targetBlockHeight-1);
        // 目标区块的上一个区块如果不是一个周期的末尾，说明一个周期尚未结束，此时目标区块难度和[目标区块的上一个区块]的难度相同。
        if (targetBlockPreviousBlock.getHeight() % GlobalSetting.IncentiveConstant.INTERVAL_BLOCK_COUNT != 0){
            targetDifficult = targetBlockPreviousBlock.getDifficulty();
            return targetDifficult;
        }

        // *** 计算新周期难度值 ***
        // 此时，targetBlockPreviousBlock是上一个周期的最后一个区块。
        // 上个周期的最后一个区块
        Block previousIntervalLastBlock = targetBlockPreviousBlock;
        // 上上个周期最后一个区块
        Block previousPreviousIntervalLastBlock = blockchainDataBase.queryBlockByBlockHeight(previousIntervalLastBlock.getHeight()-GlobalSetting.IncentiveConstant.INTERVAL_BLOCK_COUNT);
        // 上个周期出块实际耗时
        long previousIntervalActualTimespan = previousIntervalLastBlock.getTimestamp() - previousPreviousIntervalLastBlock.getTimestamp();

        /**
         * 假设有一款随机数生成器，其每秒钟生成1个随机数，其随机数范围是0到9999。
         * 那么每一次生成的随机数的值  < 1      的概率是: 1/10000
         * 那么每一次生成的随机数的值  < 10     的概率是: 10/10000
         * 那么每一次生成的随机数的值  < 100    的概率是: 100/10000
         * 那么每一次生成的随机数的值  < 1000   的概率是: 1000/10000
         * 那么每一次生成的随机数的值  < M      的概率是: M/10000
         * 那么每一次生成的随机数的范围是[0,M)  的概率是: M/10000
         *
         * 第一个周期
         * 现在我有一个随机数生成器。
         * 随机数生成器能控制在大约100秒前后(不要生成的太快，例如1秒中就生成出来了，
         * 也不要生成的太慢，例如1000秒还没有生成出来)生成出来一个在范围[0到X)内的随机数吗？
         * 如果能，那么这个X是多少？
         * 随机数生成器每秒1个随机数，希望100秒生出来，也就是要随机100秒/(1秒/1次)=100次生成，那每次的概率需要是1/100=0.01，X=10000*0.01=100。
         *
         * 第二个周期
         * 那么现在我有10个随机数生成器。
         * X如果是上次计算的100，那么需要花费多少时间随机数生成器(10个生成器只要有一个生成就可以了)才能生成满足[0-100)的数。
         * 根据上次计算需要生成100次，100次/10个随机数生成器/(1次/1秒)=10秒
         *
         * 第三个周期
         * 那么X应该如何改变，能保证10个随机数生成器也能控制在大约100秒前后生成出来一个在范围[0到X)内的随机数？
         * 如果能，那么这个X是多少？
         * 100(第二周期难度)*10秒(第二周期实际耗时)/100秒(目标耗时)=10
         *
         * X就是难度。
         * 增加随机数生成器相当于，矿工增多了，所以难度也增加了。
         */
        BigInteger bigIntegerTargetDifficult =
                new BigInteger(previousIntervalLastBlock.getDifficulty(),16)
                        .multiply(new BigInteger(String.valueOf(previousIntervalActualTimespan)))
                        .divide(new BigInteger(String.valueOf(GlobalSetting.IncentiveConstant.INTERVAL_TIME)));
        return bigIntegerTargetDifficult.toString(16);
    }
}
