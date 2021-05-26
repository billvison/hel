package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryUnconfirmedTransactionsResponse {

    private List<UnconfirmedTransactionVo> transactionVos;


    //region get set
    public List<UnconfirmedTransactionVo> getTransactionVos() {
        return transactionVos;
    }
    public void setTransactionVos(List<UnconfirmedTransactionVo> transactionVos) {
        this.transactionVos = transactionVos;
    }
    //endregion
}
