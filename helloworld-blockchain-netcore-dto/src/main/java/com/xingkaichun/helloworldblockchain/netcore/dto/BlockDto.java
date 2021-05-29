package com.xingkaichun.helloworldblockchain.netcore.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 区块
 * 属性含义参考
 * @see com.xingkaichun.helloworldblockchain.core.model.Block
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockDto implements Serializable {

    //区块产生的时间戳
    private long timestamp;
    //上一个区块的哈希
    private String previousHash;
    //区块里的交易
    private List<TransactionDto> transactions;
    //随机数
    private String nonce;




    //region get set

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    //endregion
}
