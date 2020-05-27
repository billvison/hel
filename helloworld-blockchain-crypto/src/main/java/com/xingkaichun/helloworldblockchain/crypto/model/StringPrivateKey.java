package com.xingkaichun.helloworldblockchain.crypto.model;

import java.io.Serializable;

/**
 * 字符串格式的私钥
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class StringPrivateKey implements Serializable {

    private String value;

    public StringPrivateKey(String value) {
        this.value = value;
    }




    //region get set

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //endregion
}
