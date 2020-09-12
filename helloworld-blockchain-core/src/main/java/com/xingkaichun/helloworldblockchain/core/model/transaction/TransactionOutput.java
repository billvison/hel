package com.xingkaichun.helloworldblockchain.core.model.transaction;


import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易输出
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionOutput implements Serializable {

    /**
     * 交易时间戳
     * 冗余 可以从com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction类timestamp字段获取
     */
    private long timestamp;
    /**
     * 交易输出的Hash是交易输出的摘要。交易输出的哈希确定了，具体的交易输出也就确定了。
     *
     * 区块链系统不允许同一个[交易输出的Hash]被使用两次或是两次以上。
     * 这个值的构成应当足够简单去验证这个值是否是唯一的。
     * 当区块数据足够庞大时，用户节点只有最近一部分区块与UTXO数据，这时节点必须也可以校验它的唯一性。
     * 这里建议它的构成是一串字符+时间戳。
     * 最近的区块只包含最近产生的交易，因此只要有最近的区块就可校验它的唯一性。
     *
     * 这个字段也可以用来表示一张独一无二编号的支票
     * 还有另外一个独一无二的编号，区块高度+交易在区块中的编号+交易输出在交易中编号，这个编号有个缺点，只能在区块完全确定后，才能确定这个编号
     * //TODO + 交易输出在交易中的序号
     */
    private String transactionOutputHash;
    //交易输出的地址
    //TODO 多余
    private String address;
    //交易输出的金额
    private BigDecimal value;
    /**
     * 脚本锁
     * 交易输出不应该是任何用户都可以使用的，只有能证明这个交易输出属于该用户的用户才可以使用这个交易输出。
     * 如何证明用户拥有这个交易输出？
     * 这里我们给交易输出加上一把锁，自然拥有锁对应钥匙的用户可以使用这个交易输出。
     */
    private ScriptLock scriptLock;

    /**
     * 交易所在区块的区块高度
     * 冗余
     */
    private long blockHeight;
    /**
     * 交易输出在的交易在所在的区块中的交易序列号
     * 冗余
     * 在这个交易区块中的的排序号
     */
    private long transactionSequenceNumberInBlock;
    /**
     * 交易输出序列号
     * 冗余
     * 在这个交易中的的排序号
     */
    private long transactionOutputSequence;




    //region get set

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionOutputHash() {
        return transactionOutputHash;
    }

    public void setTransactionOutputHash(String transactionOutputHash) {
        this.transactionOutputHash = transactionOutputHash;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public ScriptLock getScriptLock() {
        return scriptLock;
    }

    public void setScriptLock(ScriptLock scriptLock) {
        this.scriptLock = scriptLock;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public long getTransactionSequenceNumberInBlock() {
        return transactionSequenceNumberInBlock;
    }

    public void setTransactionSequenceNumberInBlock(long transactionSequenceNumberInBlock) {
        this.transactionSequenceNumberInBlock = transactionSequenceNumberInBlock;
    }

    public long getTransactionOutputSequence() {
        return transactionOutputSequence;
    }

    public void setTransactionOutputSequence(long transactionOutputSequence) {
        this.transactionOutputSequence = transactionOutputSequence;
    }

//endregion
}
