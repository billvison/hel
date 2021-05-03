package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

public class GetTransactionResponse {

    private TransactionDTO transaction;

    public TransactionDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDTO transaction) {
        this.transaction = transaction;
    }
}
