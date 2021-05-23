package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionOutputByTransactionOutputIdRequest {

    private TransactionOutputId transactionOutputId;


    public TransactionOutputId getTransactionOutputId() {
        return transactionOutputId;
    }

    public void setTransactionOutputId(TransactionOutputId transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
