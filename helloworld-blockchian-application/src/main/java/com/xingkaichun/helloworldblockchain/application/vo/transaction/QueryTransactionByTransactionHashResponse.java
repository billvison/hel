package com.xingkaichun.helloworldblockchain.application.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionByTransactionHashResponse {

    private TransactionView transactionView;




    //region get set

    public TransactionView getTransactionView() {
        return transactionView;
    }

    public void setTransactionView(TransactionView transactionView) {
        this.transactionView = transactionView;
    }


    //endregion

}
