package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

/**
 * 矿工:计算挖矿奖励、计算挖矿难度、挖矿、校验区块数据的合法性、将挖到的矿放进区块链上、同步其它区块链节点的数据......
 */
public interface Miner {

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

    boolean isActive() throws Exception;
    //endregion


    //region 挖矿奖励
    /**
     * 构建区块的挖矿奖励交易
     * @param block 目标区块
     */
    Transaction buildMineAwardTransaction(BlockChainDataBase blockChainDataBase, Block block) ;
    //endregion
}