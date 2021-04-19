package com.xingkaichun.helloworldblockchain.explorer.vo.transaction;

public class TransactionOutputDetailView {
    private long value;
    private boolean isSpent;
    private String transactionType;


    private long fromBlockHeight;
    private String fromBlockHash;
    private String fromTransactionHash;
    private long fromTransactionOutputIndex;
    private String fromOutputScript;


    private long toBlockHeight;
    private String toBlockHash;
    private String toTransactionHash;
    private long toTransactionInputIndex;
    private String toInputScript;

    private TransactionView inputTransaction;
    private TransactionView outputTransaction;

    public boolean isSpent() {
        return isSpent;
    }

    public void setSpent(boolean spent) {
        isSpent = spent;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getFromBlockHeight() {
        return fromBlockHeight;
    }

    public void setFromBlockHeight(long fromBlockHeight) {
        this.fromBlockHeight = fromBlockHeight;
    }

    public String getFromBlockHash() {
        return fromBlockHash;
    }

    public void setFromBlockHash(String fromBlockHash) {
        this.fromBlockHash = fromBlockHash;
    }

    public String getFromTransactionHash() {
        return fromTransactionHash;
    }

    public void setFromTransactionHash(String fromTransactionHash) {
        this.fromTransactionHash = fromTransactionHash;
    }

    public long getFromTransactionOutputIndex() {
        return fromTransactionOutputIndex;
    }

    public void setFromTransactionOutputIndex(long fromTransactionOutputIndex) {
        this.fromTransactionOutputIndex = fromTransactionOutputIndex;
    }

    public String getFromOutputScript() {
        return fromOutputScript;
    }

    public void setFromOutputScript(String fromOutputScript) {
        this.fromOutputScript = fromOutputScript;
    }

    public String getToInputScript() {
        return toInputScript;
    }

    public void setToInputScript(String toInputScript) {
        this.toInputScript = toInputScript;
    }

    public TransactionView getInputTransaction() {
        return inputTransaction;
    }

    public void setInputTransaction(TransactionView inputTransaction) {
        this.inputTransaction = inputTransaction;
    }

    public TransactionView getOutputTransaction() {
        return outputTransaction;
    }

    public void setOutputTransaction(TransactionView outputTransaction) {
        this.outputTransaction = outputTransaction;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public long getToBlockHeight() {
        return toBlockHeight;
    }

    public void setToBlockHeight(long toBlockHeight) {
        this.toBlockHeight = toBlockHeight;
    }

    public String getToBlockHash() {
        return toBlockHash;
    }

    public void setToBlockHash(String toBlockHash) {
        this.toBlockHash = toBlockHash;
    }

    public String getToTransactionHash() {
        return toTransactionHash;
    }

    public void setToTransactionHash(String toTransactionHash) {
        this.toTransactionHash = toTransactionHash;
    }

    public long getToTransactionInputIndex() {
        return toTransactionInputIndex;
    }

    public void setToTransactionInputIndex(long toTransactionInputIndex) {
        this.toTransactionInputIndex = toTransactionInputIndex;
    }
}
