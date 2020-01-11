package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

/**
 * 保存一定时间内(例如一年之内)产生的[所有已知的(已经被放进区块链)或是(未来可能放进区块链)]的交易。
 * 这种交易一定是(签名成功、输入大于输出)的交易，但不要求输入一定是UTXO。
 *
 * 为什么要保存这种交易？
 * 例如B交易的输入是A交易的输出，但是本区块链的区块链里并没有A交易(可能节点并没有收集到A交易，
 * 可能节点收集到了A交易，但是还没有挖矿成功)，因此不能武断的判定B交易就是非法的，而直接将B交易丢弃。
 *
 * 应当有一个策略，处理这种情形。
 * 这里处理的策略是：周期性的将这里的交易直接同步至{@link MinerTransactionDataBase}
 */
public interface TransactionDataBase {

    /**
     * 新增交易
     */
    void insertTransaction(Transaction transaction) throws Exception ;

    void insertBlock(Block block) throws Exception ;
}
