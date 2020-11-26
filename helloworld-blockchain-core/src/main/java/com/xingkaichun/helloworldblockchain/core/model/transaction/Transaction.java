package com.xingkaichun.helloworldblockchain.core.model.transaction;


import java.io.Serializable;
import java.util.List;

/**
 * 交易
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class Transaction implements Serializable {

    /**
     * 交易的Hash等于交易的摘要。交易的哈希确定了，具体的交易也就确定了。
     *
     * 这里强制要求区块链系统不允许同一个[交易的Hash]被使用两次或是两次以上。
     * 这个值的构成应当足够简单去验证这个值是否是唯一的。
     * 当区块数据足够庞大时，用户节点只有最近一部分区块与UTXO数据，这时节点必须也可以校验它的唯一性。
     * 这里建议它的构成是一串字符+时间戳。
     * 最近的区块只包含最近产生的交易，因此只要有最近的区块就可校验它的唯一性。
     *
     * 这个字段也可以用来表示一个独一无二的交易编号
     * 还有另外一个独一无二的编号，区块高度+交易在区块中的编号，这个编号有个缺点，只能在区块完全确定后，才能确定这个编号
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     *
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
     * 在区块中的交易序列号
     *
     * 冗余字段，这个值可以由区块链系统推算出来
     */
    private long transactionIndexInBlock;
    /**
     * 在区块链中交易序列号
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
