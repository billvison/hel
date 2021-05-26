package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class GetTransactionResponse {

    private TransactionDto transaction;

    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }
}
