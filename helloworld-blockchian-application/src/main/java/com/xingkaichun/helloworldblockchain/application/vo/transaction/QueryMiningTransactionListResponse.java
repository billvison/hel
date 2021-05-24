package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionListResponse {

    private List<MiningTransactionVo> transactionDtoList;


    //region get set
    public List<MiningTransactionVo> getTransactionDtoList() {
        return transactionDtoList;
    }
    public void setTransactionDtoList(List<MiningTransactionVo> transactionDtoList) {
        this.transactionDtoList = transactionDtoList;
    }
    //endregion
}
