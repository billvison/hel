package com.xingkaichun.helloworldblockchain.node.transport.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * 区块
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockDTO implements Serializable {
    private long timestamp;
    private BigInteger height;
    private List<TransactionDTO> transactions;
    private String consensusValue;




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

    public String getConsensusValue() {
        return consensusValue;
    }

    public void setConsensusValue(String consensusValue) {
        this.consensusValue = consensusValue;
    }

    //endregion
}
