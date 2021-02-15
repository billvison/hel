package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

/**
 *
 * @author 邢开春
 */
public class QueryMiningTransactionByTransactionHashRequest {

    private String transactionHash;




    //region get set

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    //endregion
}
