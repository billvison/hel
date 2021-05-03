package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

public class PostTransactionRequest {

    private TransactionDTO transaction;

    public TransactionDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDTO transaction) {
        this.transaction = transaction;
    }
}
