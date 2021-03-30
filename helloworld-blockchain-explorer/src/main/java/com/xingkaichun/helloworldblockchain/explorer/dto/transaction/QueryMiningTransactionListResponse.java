package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionListResponse {

    private List<MiningTransactionDTO> transactionDtoList;


    //region get set
    public List<MiningTransactionDTO> getTransactionDtoList() {
        return transactionDtoList;
    }
    public void setTransactionDtoList(List<MiningTransactionDTO> transactionDtoList) {
        this.transactionDtoList = transactionDtoList;
    }
    //endregion
}
