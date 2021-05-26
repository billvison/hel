package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionListByBlockHashTransactionHeightResponse {

    private List<TransactionVo> transactionVos;




    //region get set

    public List<TransactionVo> getTransactionVos() {
        return transactionVos;
    }

    public void setTransactionVos(List<TransactionVo> transactionVos) {
        this.transactionVos = transactionVos;
    }


    //endregion
}
