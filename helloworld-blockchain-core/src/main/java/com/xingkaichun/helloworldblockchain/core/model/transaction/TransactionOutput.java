package com.xingkaichun.helloworldblockchain.core.model.transaction;


import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;

import java.io.Serializable;

/**
 * 交易输出：交易的收款方叫作交易输出。
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionOutput extends TransactionOutputId implements Serializable {

    /**
     * 交易输出的金额
     */
    private long value;
    /**
     * [输出脚本]
     * [交易输出]不应该是任何用户都可以使用的，(只有证明了拥有[交易输出]所有权)的用户才可以使用这个[交易输出]。
     * 如何证明用户拥有这个[交易输出]？
     * [输出脚本]相当于一把锁，[输入脚本]相当于一把钥匙，
     * 自然当钥匙可以打开锁时，就可以证明能拿出钥匙[输入脚本]的用户拥有锁[输出脚本]。
     * [输出脚本]并不是真的锁，[输入脚本]也不是真的钥匙，回归本质，它们是一串代码
     * ，所谓代码(输入脚本)解锁代码(输出脚本)的含义是，[输入脚本]与[输出脚本]组合成一个[输入输出脚本]
     * ，执行输入输出脚本，执行的结果符合预期，即代码层面的解锁。
     * 谁来执行[输入输出脚本]
     * @see com.xingkaichun.helloworldblockchain.core.VirtualMachine
     */
    private OutputScript outputScript;

    /**
     * 交易输出的地址
     * 冗余；可以从[输出脚本]解析出地址
     */
    private String address;

    /**
     * [[产生交易输出的]交易所在的]区块的区块高度
     * 冗余
     */
    private long blockHeight;
    /**
     * [[产生交易输出的]交易所在的]区块的区块哈希
     * 冗余
     */
    private String blockHash;
    /**
     * [[产生交易输出的]交易在]区块链中的高度
     * 冗余
     */
    private long transactionHeight;
    /**
     * [[产生交易输出的]交易在]区块中的交易序列号
     * 冗余
     * 在这个交易区块中的的排序号
     */
    private long transactionIndex;
    /**
     * [交易输出]在区块链中的高度，这是一个全局高度，区块链系统中的第一笔[交易输出]高度为1，其后交易输出高度依次递增1。
     * 冗余
     */
    private long transactionOutputHeight;



    //region get set

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public OutputScript getOutputScript() {
        return outputScript;
    }

    public void setOutputScript(OutputScript outputScript) {
        this.outputScript = outputScript;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public long getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(long transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public long getTransactionOutputHeight() {
        return transactionOutputHeight;
    }

    public void setTransactionOutputHeight(long transactionOutputHeight) {
        this.transactionOutputHeight = transactionOutputHeight;
    }

    public long getTransactionHeight() {
        return transactionHeight;
    }

    public void setTransactionHeight(long transactionHeight) {
        this.transactionHeight = transactionHeight;
    }
    //endregion
}
