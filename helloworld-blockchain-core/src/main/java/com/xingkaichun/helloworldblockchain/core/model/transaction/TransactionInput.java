package com.xingkaichun.helloworldblockchain.core.model.transaction;

import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;

import java.io.Serializable;

/**
 * 交易输入
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TransactionInput implements Serializable {

    //交易的输入是一笔交易的输出
    private TransactionOutput unspendTransactionOutput;
    //脚本钥匙
    private ScriptKey scriptKey;




    //region get set

    public TransactionOutput getUnspendTransactionOutput() {
        return unspendTransactionOutput;
    }

    public void setUnspendTransactionOutput(TransactionOutput unspendTransactionOutput) {
        this.unspendTransactionOutput = unspendTransactionOutput;
    }

    public ScriptKey getScriptKey() {
        return scriptKey;
    }

    public void setScriptKey(ScriptKey scriptKey) {
        this.scriptKey = scriptKey;
    }

    //endregion
}