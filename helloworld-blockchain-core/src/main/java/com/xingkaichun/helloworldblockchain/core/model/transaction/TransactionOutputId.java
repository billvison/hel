package com.xingkaichun.helloworldblockchain.core.model.transaction;

import java.io.Serializable;

/**
 * 交易输出ID
 *
 * @author 邢开春
 */
public class TransactionOutputId implements Serializable {
    /**
     * 交易哈希
     * 冗余
     */
    private String transactionHash;
    /**
     * 交易输出在[此笔交易所有的交易输出]中的序列号
     * 冗余
     */
    private long transactionOutputIndex;


    public String getTransactionOutputId() {
        return transactionHash + "|" + transactionOutputIndex;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
}
