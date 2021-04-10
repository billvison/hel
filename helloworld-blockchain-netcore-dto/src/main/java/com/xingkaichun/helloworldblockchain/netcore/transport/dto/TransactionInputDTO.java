package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

import java.io.Serializable;

/**
 * 交易输入
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.bo.transaction.TransactionInput
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionInputDTO implements Serializable {

    //交易哈希
    private String transactionHash;
    //交易输出的索引
    private long transactionOutputIndex;
    //[输入脚本]
    private InputScriptDTO inputScript;




    //region get set
    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getTransactionOutputIndex() {
        return transactionOutputIndex;
    }

    public void setTransactionOutputIndex(long transactionOutputIndex) {
        this.transactionOutputIndex = transactionOutputIndex;
    }

    public InputScriptDTO getInputScript() {
        return inputScript;
    }

    public void setInputScript(InputScriptDTO inputScript) {
        this.inputScript = inputScript;
    }

//endregion
}