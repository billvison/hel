package com.xingkaichun.helloworldblockchain.application.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionByTransactionHashResponse {

    private TransactionVo transactions;




    //region get set

    public TransactionVo getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionVo transactions) {
        this.transactions = transactions;
    }


    //endregion

}
