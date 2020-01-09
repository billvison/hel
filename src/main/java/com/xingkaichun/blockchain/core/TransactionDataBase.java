package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

/**
 * 保存所有正常的交易(签名成功、输入大于输出)的交易
 *
 //TODO 异常交易不应该彻底丢掉。
 // 例如B交易依赖A交易，但是本区块链并没有成功同步到A交易，因此而判定B交易就是非法的，而将其丢弃。
 // 应当有一个策略，处理这种情形。
 */
public interface TransactionDataBase {

    /**
     * 新增交易
     */
    void insertTransaction(Transaction transaction) throws Exception ;

    void insertBlock(Block block) throws Exception ;
}
