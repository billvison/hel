package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;

import java.util.List;

/**
 * 未确认交易数据库
 * 所有没有持久化到区块链的交易，都应该尽可能的被收集起来。
 * 其它对象可以从本类获取未确认交易数据，然后进行自己的活动。例如矿工可以从该类获取挖矿的原材料(未确认交易数据)进行挖矿活动。
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class UnconfirmedTransactionDatabase {

    /**
     * 新增交易
     */
    public abstract void insertTransaction(TransactionDto transactionDto) ;

    /**
     * 批量提取交易
     */
    public abstract List<TransactionDto> selectTransactions(long from, long size) ;

    /**
     * 删除交易
     */
    public abstract void deleteByTransactionHash(String transactionHash) ;

    /**
     * 查询交易
     */
    public abstract TransactionDto selectTransactionByTransactionHash(String transactionHash);
}
