package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionListResponse {

    private List<Transaction> transactionDtoList;




    //region get set

    public List<Transaction> getTransactionDtoList() {
        return transactionDtoList;
    }

    public void setTransactionDtoList(List<Transaction> transactionDtoList) {
        this.transactionDtoList = transactionDtoList;
    }

    //endregion
}
