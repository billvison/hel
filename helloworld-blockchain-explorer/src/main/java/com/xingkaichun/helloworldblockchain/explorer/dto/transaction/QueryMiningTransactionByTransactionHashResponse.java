package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionByTransactionHashResponse {

    private MiningTransactionDTO transactionDTO;

    //region get set

    public MiningTransactionDTO getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(MiningTransactionDTO transactionDTO) {
        this.transactionDTO = transactionDTO;
    }


    //endregion

}
