package com.xingkaichun.helloworldblockchain.netcore.dto;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class GetUnconfirmedTransactionsResponse {

    private List<TransactionDto> transactions;

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }
}
