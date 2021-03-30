package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionListResponse {

    private List<MiningTransactionView> transactionDtoList;


    //region get set
    public List<MiningTransactionView> getTransactionDtoList() {
        return transactionDtoList;
    }
    public void setTransactionDtoList(List<MiningTransactionView> transactionDtoList) {
        this.transactionDtoList = transactionDtoList;
    }
    //endregion
}
