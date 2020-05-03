package com.xingkaichun.helloworldblockchain.crypto.model;

import java.io.Serializable;

/**
 * 地址
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class StringAddress implements Serializable {

    private String value;

    public StringAddress(String value) {
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
