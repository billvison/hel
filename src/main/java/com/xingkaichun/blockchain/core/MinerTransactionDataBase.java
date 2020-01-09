package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 * 该类的作用是收集交易，用于挖矿。
 * 所有没有被成功挖矿而放进区块链的交易，都应该被收集起来，供挖矿使用。
 * 其它对象可以从本类获取交易数据，然后进行自己的活动。例如矿工可以从该类获取挖矿的原材料(交易数据)进行挖矿活动。
 * 只有正确的交易才应该放进该类。
 * //TODO 需要设计一个数据库，用于保存所有的签名正确的交易。
 */
public interface MinerTransactionDataBase {

    /**
     * 新增交易
     */
    void insertTransaction(Transaction transaction) throws Exception ;

    /**
     * 新增交易
     */
    void insertTransactionList(List<Transaction> transactionList) throws Exception ;

    /**
     * 获取交易
     */
    List<Transaction> selectTransactionList(int from, int size) throws Exception ;

    /**
     * 删除交易
     */
    void deleteTransaction(Transaction transaction) throws Exception ;

    /**
     * 删除交易
     */
    void deleteTransactionList(List<Transaction> transactionList) throws Exception ;
}
