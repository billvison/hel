package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.model.Block;
import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.model.transaction.Transaction;
import lombok.Data;

/**
 * 矿工:挖矿、分配挖矿奖励
 */
@Data
public abstract class Miner {

    //矿工地址
    protected StringAddress minerStringAddress;
    // 矿工挖矿所在的区块链
    protected BlockChainDataBase blockChainDataBase ;
    //矿工交易数据库：矿工从交易数据库里获取挖矿的原材料(交易数据)
    protected MinerTransactionDtoDataBase minerTransactionDtoDataBase;

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
    public abstract void resume() ;

    /**
     * 矿工是否处于活动状态。
     */
    public abstract boolean isActive() ;
    //endregion


    //region 挖矿奖励
    /**
     * 构建区块的挖矿奖励交易，这里可以实现挖矿奖励的分配。
     * @param block 目标区块
     */
    public abstract Transaction buildMineAwardTransaction(BlockChainDataBase blockChainDataBase, Block block) ;
    //endregion
}