package com.xingkaichun.helloworldblockchain.node.dto.transaction;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
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
