package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * 区块
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.model.Block
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockDTO implements Serializable {
    //区块产生的时间戳
    private long timestamp;
    //区块高度
    private BigInteger height;
    //区块里的交易
    private List<TransactionDTO> transactions;
    //共识值
    private String nonce;




    //region get set

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public BigInteger getHeight() {
        return height;
    }

    public void setHeight(BigInteger height) {
        this.height = height;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
//endregion
}
