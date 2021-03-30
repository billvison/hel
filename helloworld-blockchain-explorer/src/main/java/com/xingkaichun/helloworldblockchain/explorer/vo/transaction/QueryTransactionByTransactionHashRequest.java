package com.xingkaichun.helloworldblockchain.explorer.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTransactionByTransactionHashRequest {

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
