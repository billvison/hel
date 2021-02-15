package com.xingkaichun.helloworldblockchain.core.model.transaction;


import java.io.Serializable;
import java.util.List;

/**
 * 交易
 *
 * @author 邢开春
 */
public class Transaction implements Serializable {

    /**
     * 交易哈希
     * 交易哈希用来表示一个独一无二的交易编号。
     * 交易哈希是交易的摘要。可以认为交易哈希和交易一一对应。交易确定了，交易哈希也就确定了。交易哈希确定了，交易也就确定了。
     * 这里要求区块链系统不允许同一个哈希被使用两次或是两次以上(一个哈希同时被交易哈希、区块哈希使用也不行)。
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     * 相关拓展：比特币曾经出现过两个交易ID相同的情况。https://zhuanlan.zhihu.com/p/258955441
     */
    private String transactionHash;
    /**
     * 交易类型
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private TransactionType transactionType;
    /**
     * 交易输入
     */
    private List<TransactionInput> inputs;
    /**
     * 交易输出
     */
    private List<TransactionOutput> outputs;
    /**
     * 交易在区块中的序列号
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long transactionIndexInBlock;
    /**
     * 交易在区块链中的序列号
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long transactionIndexInBlockchain;
    /**
     * 交易所在区块的区块高度
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long blockHeight;




    //region get set

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

    public long getTransactionIndexInBlock() {
        return transactionIndexInBlock;
    }

    public void setTransactionIndexInBlock(long transactionIndexInBlock) {
        this.transactionIndexInBlock = transactionIndexInBlock;
    }

    public long getTransactionIndexInBlockchain() {
        return transactionIndexInBlockchain;
    }

    public void setTransactionIndexInBlockchain(long transactionIndexInBlockchain) {
        this.transactionIndexInBlockchain = transactionIndexInBlockchain;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    //endregion
}
