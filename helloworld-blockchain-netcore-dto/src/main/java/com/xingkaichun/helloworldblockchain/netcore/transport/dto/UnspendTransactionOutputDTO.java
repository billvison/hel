package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

import java.io.Serializable;

/**
 * 未花费交易输出
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.model.transaction.UnspendTransactionOutput
 *
 * @author 邢开春
 */
public class UnspendTransactionOutputDTO implements Serializable {

    //交易哈希
    private String transactionHash;
    //交易输出的索引
    private long transactionOutputIndex;




    //region get set
    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }
    //end
}
