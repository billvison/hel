package com.xingkaichun.helloworldblockchain.core.model.key;

import lombok.Data;

import java.io.Serializable;

/**
 * 私钥
 */
@Data
public class StringPrivateKey implements Serializable {

    private String value;

    public StringPrivateKey(String value) {
        this.value = value;
    }
}
