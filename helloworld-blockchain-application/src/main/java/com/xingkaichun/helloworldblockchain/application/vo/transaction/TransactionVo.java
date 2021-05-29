package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionVo {
    private long blockHeight;
    private String blockHash;
    private long confirmCount;
    private String transactionHash;
    private String blockTime;

    private long transactionFee;
    private String transactionType;
    private long transactionInputCount;
    private long transactionOutputCount;
    private long transactionInputValues;
    private long transactionOutputValues;

    private List<TransactionInputVo> transactionInputs;
    private List<TransactionOutputVo> transactionOutputs;

    private List<String> inputScripts;
    private List<String> outputScripts;


    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public long getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(long confirmCount) {
        this.confirmCount = confirmCount;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }

    public long getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(long transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public long getTransactionInputCount() {
        return transactionInputCount;
    }

    public void setTransactionInputCount(long transactionInputCount) {
        this.transactionInputCount = transactionInputCount;
    }

    public long getTransactionOutputCount() {
        return transactionOutputCount;
    }

    public void setTransactionOutputCount(long transactionOutputCount) {
        this.transactionOutputCount = transactionOutputCount;
    }

    public long getTransactionInputValues() {
        return transactionInputValues;
    }

    public void setTransactionInputValues(long transactionInputValues) {
        this.transactionInputValues = transactionInputValues;
    }

    public long getTransactionOutputValues() {
        return transactionOutputValues;
    }

    public void setTransactionOutputValues(long transactionOutputValues) {
        this.transactionOutputValues = transactionOutputValues;
    }

    public List<TransactionInputVo> getTransactionInputs() {
        return transactionInputs;
    }

    public void setTransactionInputs(List<TransactionInputVo> transactionInputs) {
        this.transactionInputs = transactionInputs;
    }

    public List<TransactionOutputVo> getTransactionOutputs() {
        return transactionOutputs;
    }

    public void setTransactionOutputs(List<TransactionOutputVo> transactionOutputs) {
        this.transactionOutputs = transactionOutputs;
    }

    public List<String> getInputScripts() {
        return inputScripts;
    }

    public void setInputScripts(List<String> inputScripts) {
        this.inputScripts = inputScripts;
    }

    public List<String> getOutputScripts() {
        return outputScripts;
    }

    public void setOutputScripts(List<String> outputScripts) {
        this.outputScripts = outputScripts;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }
}
