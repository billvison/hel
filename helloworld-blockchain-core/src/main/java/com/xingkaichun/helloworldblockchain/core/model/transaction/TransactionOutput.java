package com.xingkaichun.helloworldblockchain.core.model.transaction;


import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;

import java.io.Serializable;

/**
 * 交易输出
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionOutput extends TransactionOutputId implements Serializable {

    /**
     * 交易输出的金额
     */
    private long value;
    /**
     * [输出脚本]
     * 交易输出不应该是任何用户都可以使用的，(只有证明了拥有交易输出所有权)的用户才可以使用这个交易输出。
     * 如何证明用户拥有这个交易输出？
     * [输出脚本]相当于一把锁，[输入脚本]相当于一把钥匙，
     * 自然当钥匙可以打开锁时，就可以证明能拿出钥匙[输入脚本]的用户拥有锁[输出脚本]。
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
     * [[产生交易输出的]交易在]区块中的交易序列号
     * 冗余
     * 在这个交易区块中的的排序号
     */
    private long transactionIndexInBlock;




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

    public long getTransactionIndexInBlock() {
        return transactionIndexInBlock;
    }

    public void setTransactionIndexInBlock(long transactionIndexInBlock) {
        this.transactionIndexInBlock = transactionIndexInBlock;
    }

    //endregion
}
