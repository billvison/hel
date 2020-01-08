package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.math.BigDecimal;

/**
 * 矿工:计算挖矿奖励、计算挖矿难度、挖矿、校验区块数据的合法性、将挖到的矿放进区块链上、同步其它区块链节点的数据......
 */
public interface Miner {

    //region 全局控制
    /**
     * 启动
     */
    void run() throws Exception ;
    /**
     * 暂停所有
     */
    void pause() throws Exception;
    /**
     * 恢复所有
     */
    void resume() throws Exception;
    //endregion



    //region 挖矿相关
    /**
     * 挖矿
     */
    void mine() throws Exception;
    /**
     * 暂停挖矿
     */
    void pauseMine() throws Exception;
    /**
     * 恢复挖矿
     */
    void resumeMine() throws Exception;
    //endregion



    //region 同步其它区块链节点的数据
    /**
     * 同步其它区块链节点的数据。
     * @throws Exception
     */
    void synchronizeBlockChainNode() throws Exception ;
    /**
     * 暂停同步其它区块链节点的数据
     */
    void pauseSynchronizeBlockChainNode();
    /**
     * 恢复同步其它区块链节点的数据
     */
    void resumeSynchronizeBlockChainNode() throws Exception;
    //endregion



    //region 区块校验
    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    boolean isBlockCanApplyToBlockChain(BlockChainDataBase blockChainDataBase, Block block) throws Exception ;
    //endregion



    //region 挖矿奖励
    /**
     * 构建区块的挖矿奖励交易
     * @param block 目标区块
     */
    Transaction buildMineAwardTransaction(BlockChainDataBase blockChainDataBase, Block block) ;
    /**
     * 获取区块中写入的挖矿奖励交易
     * @param block 区块
     * @return
     */
    Transaction obtainBlockWriteMineAwardTransaction(Block block) ;
    /**
     * 区块中写入的挖矿奖励是否正确？
     * @param block 被校验挖矿奖励是否正确的区块
     * @return
     */
    boolean isBlockWriteMineAwardRight(BlockChainDataBase blockChainDataBase, Block block);
    //endregion

    //region 区块Hash
    /**
     * 区块中写入的Hash是否正确
     */
    //TODO 应该判断nonce正确，还是hash正确
    boolean isBlockWriteHashRight(BlockChainDataBase blockChainDataBase, Block block) ;
    //endregion
}