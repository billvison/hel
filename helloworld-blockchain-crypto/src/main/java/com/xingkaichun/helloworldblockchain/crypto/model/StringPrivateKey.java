package com.xingkaichun.helloworldblockchain.crypto.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 私钥
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class StringPrivateKey implements Serializable {

    private String value;

    public StringPrivateKey(String value) {
        this.value = value;
    }
}
