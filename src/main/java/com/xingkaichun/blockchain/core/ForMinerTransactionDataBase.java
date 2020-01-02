package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 * 交易池
 * 所有没有持久化进区块链的交易，都应该放入交易池。
 * 其它对象可以从交易池获取这部分交易数据，然后进行自己的活动。例如矿工可以从交易池获取挖矿的原材料(交易数据)进行挖矿活动。
 *
 *
 */
public interface ForMinerTransactionDataBase {

    /**
     * 添加交易进交易池
     */
    boolean addTransaction(Transaction transaction) throws Exception ;

    /**
     * 从交易池获取交易
     */
    //TODO 增加按照排序选择
    List<Transaction> getTransactionList() throws Exception ;
}
