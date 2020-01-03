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
    /**
     * 校验(未打包进区块链的)交易的合法性
     * 奖励交易校验需要传入block参数
     */
    boolean checkUnBlockChainTransaction(Block block, Transaction transaction) throws Exception ;
    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在区块的角度校验交易】
     * @param packingTransactionList
    // * @return 被丢弃的异常交易
     * @throws Exception
     */
    void dropPackingTransactionException_PointOfView_Block(List<Transaction> packingTransactionList) throws Exception ;


    //region 挖矿奖励相关
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

    //region 构建区块、计算区块hash、校验区块Nonce
    /**
     * 构建缺少nonce(代表尚未被挖矿)的区块
     */
    Block buildNonNonceBlock(List<Transaction> packingTransactionList) throws Exception ;
    /**
     * 计算区块的Hash值
     * @param block 区块
     */
    String calculateBlockHash(Block block) ;
    /**
     * 计算区块的默克尔树根值
     * @param block 区块
     */
    String calculateBlockMerkleRoot(Block block) ;
    /**
     * 判断Block的挖矿的成果Nonce是否正确
     */
    boolean isBlockMinedNonceSuccess(Block block) ;
    /**
     * Hash满足挖矿难度的要求吗？
     * @param targetDificulty 目标挖矿难度
     * @param hash 需要校验的Hash
     */
    boolean isHashRight(String targetDificulty,String hash) ;
    //endregion

}
