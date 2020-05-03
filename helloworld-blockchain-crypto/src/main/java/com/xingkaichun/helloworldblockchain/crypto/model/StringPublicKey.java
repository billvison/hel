package com.xingkaichun.helloworldblockchain.crypto.model;

import java.io.Serializable;

/**
 * 公钥
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class StringPublicKey implements Serializable {

    private String value;

    public StringPublicKey(String value) {
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