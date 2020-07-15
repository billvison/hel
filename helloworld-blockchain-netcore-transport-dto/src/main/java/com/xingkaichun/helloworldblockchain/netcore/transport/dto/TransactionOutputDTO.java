package com.xingkaichun.helloworldblockchain.netcore.transport.dto;


import java.io.Serializable;
import java.util.List;

/**
 * 交易输出
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.model.TransactionOutput
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TransactionOutputDTO implements Serializable {

    //交易输出的地址
    private String address;
    //交易输出的金额
    private String value;
    //脚本锁
    private List<String> scriptLock;




    //region get set

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
