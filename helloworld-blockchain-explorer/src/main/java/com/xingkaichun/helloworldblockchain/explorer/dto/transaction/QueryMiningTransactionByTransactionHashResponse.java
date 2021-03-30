package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionByTransactionHashResponse {

    private MiningTransactionView transactionDTO;

    //region get set

    public MiningTransactionView getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(MiningTransactionView transactionDTO) {
        this.transactionDTO = transactionDTO;
    }


    //endregion

}
