package com.xingkaichun.blockchain.core;


import com.xingkaichun.blockchain.core.model.Block;

/**
 * 区块校验者
 */
public interface Checker {
    /**
     * 检测区块
     */
    boolean checkBlock(BlockChainCore blockChainCore, Block block) throws Exception;
}
