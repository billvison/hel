package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
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
