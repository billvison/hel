package com.xingkaichun.helloworldblockchain.crypto.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 地址
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class StringAddress implements Serializable {

    private String value;

    public StringAddress(String value) {
        this.value = value;
    }
}
