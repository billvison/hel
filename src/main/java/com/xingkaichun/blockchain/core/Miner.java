package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

/**
 * 矿工:挖矿、分配挖矿奖励
 */
public abstract class Miner {

    //region 挖矿相关
    /**
     * 启用矿工。
     * 矿工有两种状态：活动状态与非活动状态。
     * 若矿工处于活动作态，开始挖矿。
     * 若矿工处于非活动状态，矿工不会进行任何工作。
     */
    public abstract void start() throws Exception;

    /**
     * 设置矿工为非活动状态。
     */
    public abstract void stop() throws Exception;

    /**
     * 设置矿工为活动状态。
     */
    public abstract void resume() throws Exception;

    /**
     * 矿工是否处于活动状态。
     */
    public abstract boolean isActive() throws Exception;
    //endregion


    //region 挖矿奖励
    /**
     * 构建区块的挖矿奖励交易，这里可以实现挖矿奖励的分配。
     * @param block 目标区块
     */
    public abstract Transaction buildMineAwardTransaction(BlockChainDataBase blockChainDataBase, Block block) ;
    //endregion
}