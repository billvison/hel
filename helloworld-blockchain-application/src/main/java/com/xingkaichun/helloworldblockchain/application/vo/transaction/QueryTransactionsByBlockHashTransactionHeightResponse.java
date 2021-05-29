package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionsByBlockHashTransactionHeightResponse {

    private List<TransactionVo> transactions;




    //region get set

    public List<TransactionVo> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionVo> transactions) {
        this.transactions = transactions;
    }


    //endregion
}
