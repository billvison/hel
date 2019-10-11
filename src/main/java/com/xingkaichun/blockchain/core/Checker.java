package com.xingkaichun.blockchain.core;


import com.xingkaichun.blockchain.core.impl.LightweightBlockChain;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 * 区块校验者
 */
public interface Checker {

    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链: 区块可以是区块链上的下一个区块
     */
    boolean isBlockApplyToBlockChain(BlockChainCore blockChainCore, Block block) throws Exception;

    /**
     * 检测一串区块是否可以被应用到区块链上
     * 有两种情况，一串区块可以被应用到区块链:
     * 情况1：需要删除一部分链上的区块，然后链上可以衔接这串区块，且删除的区块数目要小于增加的区块的数目
     * 情况2：不需要删除链上的区块，链上直接可以衔接这串区块
     */
    boolean isBlockListApplyToBlockChain(BlockChainCore blockChainCore, List<Block> blockList) throws Exception;

    /**
     * 校验(未打包进区块链的)交易的合法性
     */
    boolean checkUnBlockChainTransaction(BlockChainCore blockChainCore, LightweightBlockChain blockRollback, LightweightBlockChain newBlock, Transaction transaction) throws Exception;

}
