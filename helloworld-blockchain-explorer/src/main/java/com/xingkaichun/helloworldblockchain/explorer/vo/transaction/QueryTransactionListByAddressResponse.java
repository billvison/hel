package com.xingkaichun.helloworldblockchain.explorer.vo.transaction;

import java.util.List;

/**
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionListByAddressResponse {

    private List<TransactionView> transactionViewList;




    //region get set

    public List<TransactionView> getTransactionViewList() {
        return transactionViewList;
    }

    public void setTransactionViewList(List<TransactionView> transactionViewList) {
        this.transactionViewList = transactionViewList;
    }


    //endregion
}
