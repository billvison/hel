package com.xingkaichun.helloworldblockchain.explorer.vo.transaction;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionListByTransactionHeightResponse {

    private List<Transaction> transactionList;




    //region get set

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    //endregion
}
