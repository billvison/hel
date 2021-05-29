package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryUnconfirmedTransactionsResponse {

    private List<UnconfirmedTransactionVo> transactions;


    //region get set
    public List<UnconfirmedTransactionVo> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<UnconfirmedTransactionVo> transactions) {
        this.transactions = transactions;
    }
    //endregion
}
