package com.xingkaichun.blockchain.core;


import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 * 区块校验者
 */
public abstract class Checker {

    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    public abstract boolean isBlockApplyToBlockChain(BlockChainCore blockChainCore, Block block) throws Exception;

    /**
     * 检测一串区块是否可以被应用到区块链上
     * 有两种情况，一串区块可以被应用到区块链:
     * 情况1：需要删除一部分链上的区块，然后链上可以衔接这串区块，且删除的区块数目要小于增加的区块的数目
     * 情况2：不需要删除链上的区块，链上直接可以衔接这串区块
     */
    public abstract boolean isBlockListApplyToBlockChain(BlockChainCore blockChainCore, List<Block> blockList) throws Exception;

    /**
     * 校验(未打包进区块链的)交易的合法性
     * 奖励交易校验需要传入block参数
     */
    public abstract boolean checkUnBlockChainTransaction(BlockChainCore blockChainCore, Block block, Transaction transaction) throws Exception;

}
