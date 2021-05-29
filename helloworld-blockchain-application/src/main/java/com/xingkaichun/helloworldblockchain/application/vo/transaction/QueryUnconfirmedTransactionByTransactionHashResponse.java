package com.xingkaichun.helloworldblockchain.application.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryUnconfirmedTransactionByTransactionHashResponse {

    private UnconfirmedTransactionVo transaction;

    //region get set

    public UnconfirmedTransactionVo getTransaction() {
        return transaction;
    }

    public void setTransaction(UnconfirmedTransactionVo transaction) {
        this.transaction = transaction;
    }


    //endregion

}
