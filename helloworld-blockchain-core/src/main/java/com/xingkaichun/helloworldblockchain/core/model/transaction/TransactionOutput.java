package com.xingkaichun.helloworldblockchain.core.model.transaction;


import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 交易输出
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TransactionOutput implements Serializable {

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
     */
    private String transactionOutputHash;
    //交易输出的地址
    private StringAddress stringAddress;
    //交易输出的金额
    private BigDecimal value;
    //脚本锁
    private ScriptLock scriptLock;

    /**
     * 交易所在区块的区块高度
     * 冗余
     */
    private BigInteger blockHeight;
    /**
     * 交易输出在的交易在所在的区块中的交易序列号
     * 冗余
     * 在这个交易区块中的的排序号
     */
    private BigInteger transactionSequenceNumberInBlock;
    /**
     * 交易输出序列号
     * 冗余
     * 在这个交易中的的排序号
     */
    private BigInteger transactionOutputSequence;




    //region get set

    public String getTransactionOutputHash() {
        return transactionOutputHash;
    }

    public void setTransactionOutputHash(String transactionOutputHash) {
        this.transactionOutputHash = transactionOutputHash;
    }

    public StringAddress getStringAddress() {
        return stringAddress;
    }

    public void setStringAddress(StringAddress stringAddress) {
        this.stringAddress = stringAddress;
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

    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(BigInteger blockHeight) {
        this.blockHeight = blockHeight;
    }

    public BigInteger getTransactionSequenceNumberInBlock() {
        return transactionSequenceNumberInBlock;
    }

    public void setTransactionSequenceNumberInBlock(BigInteger transactionSequenceNumberInBlock) {
        this.transactionSequenceNumberInBlock = transactionSequenceNumberInBlock;
    }

    public BigInteger getTransactionOutputSequence() {
        return transactionOutputSequence;
    }

    public void setTransactionOutputSequence(BigInteger transactionOutputSequence) {
        this.transactionOutputSequence = transactionOutputSequence;
    }

    //endregion
}
