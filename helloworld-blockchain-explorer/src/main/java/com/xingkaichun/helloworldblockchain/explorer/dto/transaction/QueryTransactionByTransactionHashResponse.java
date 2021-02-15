package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

/**
 *
 * @author 邢开春
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
