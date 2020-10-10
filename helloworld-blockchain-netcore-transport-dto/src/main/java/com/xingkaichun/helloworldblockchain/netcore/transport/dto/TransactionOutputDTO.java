package com.xingkaichun.helloworldblockchain.netcore.transport.dto;


import java.io.Serializable;
import java.util.List;

/**
 * 交易输出
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.model.TransactionOutput
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionOutputDTO implements Serializable {

    //交易输出的金额
    private long value;
    //脚本锁
    private List<String> scriptLock;




    //region get set

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public List<String> getScriptLock() {
        return scriptLock;
    }

    public void setScriptLock(List<String> scriptLock) {
        this.scriptLock = scriptLock;
    }

    //endregion
}
