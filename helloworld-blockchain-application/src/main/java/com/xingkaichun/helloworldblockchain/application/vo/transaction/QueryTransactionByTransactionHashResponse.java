package com.xingkaichun.helloworldblockchain.application.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionByTransactionHashResponse {

    private TransactionVo transaction;




    //region get set
    public TransactionVo getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionVo transaction) {
        this.transaction = transaction;
    }
    //endregion

}
