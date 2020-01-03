package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * 矿工:挖矿、计算挖矿奖励、计算挖矿难度、校验交易数据的合法性、校验区块数据的合法性。
 */
public interface Miner {

    //region 挖矿相关:启动挖矿线程、停止挖矿线程、跳过正在挖的矿
    /**
     * 启动
     */
    void running() throws Exception ;
    //endregion

    /**
     * 同步其它区块链的数据。
     * @return 如果真的同步了其它区块链的数据，也就是本区块链新增了区块，返回true；其它情况，返回false。
     * @throws Exception
     */
    boolean synchronizeBlockChainNode() throws Exception ;
    /**
     * 构建缺少nonce(代表尚未被挖矿)的区块
     */
    Block buildNonNonceBlock(List<Transaction> packingTransactionList) throws Exception ;
    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    boolean isBlockApplyToBlockChain(Block block) throws Exception ;
    /**
     * 检测一串区块是否可以被应用到区块链上
     * 有两种情况，一串区块可以被应用到区块链:
     * 情况1：需要删除一部分链上的区块，然后链上可以衔接这串区块，且删除的区块数目要小于增加的区块的数目
     * 情况2：不需要删除链上的区块，链上直接可以衔接这串区块
     */
    boolean isBlockListApplyToBlockChain(List<Block> blockList) throws Exception ;

    //region 挖矿奖励
    /**
     * 构建区块的挖矿奖励交易
     * @param block 目标区块
     */
    Transaction buildMineAwardTransaction(Block block) ;
    /**
     * 获取区块中写入的挖矿奖励
     * @param block 区块
     * @return
     */
    BigDecimal obtainBlockWriteMineAward(Block block) ;
    /**
     * 区块的挖矿奖励是否正确？
     * @param block 被校验挖矿奖励是否正确的区块
     * @return
     */
    boolean isBlockMineAwardRight(Block block);
    //endregion

    //region 默克尔树根
    /**
     * 计算区块的默克尔树根值
     * @param block 区块
     */
    String calculateBlockMerkleRoot(Block block) ;
    /**
     * 判断Block写入的默克尔树根是否正确
     */
    boolean isBlockMerkleRootRight(Block block);
    //endregion

    //region 区块Hash
    /**
     * 计算区块的Hash值
     * @param block 区块
     */
    String calculateBlockHash(Block block) ;
    /**
     * 判断挖矿Hash是否正确
     */
    boolean isBlockHashRight(Block block) ;
    //endregion
}
