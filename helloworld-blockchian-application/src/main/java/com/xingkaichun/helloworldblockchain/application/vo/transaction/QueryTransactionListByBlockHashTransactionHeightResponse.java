package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionListByBlockHashTransactionHeightResponse {

    private List<TransactionVo> transactionVoList;




    //region get set

    public List<TransactionVo> getTransactionVoList() {
        return transactionVoList;
    }

    public void setTransactionVoList(List<TransactionVo> transactionVoList) {
        this.transactionVoList = transactionVoList;
    }


    //endregion
}
