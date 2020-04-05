package com.xingkaichun.helloworldblockchain.crypto.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 公钥
 */
@Data
public class StringPublicKey implements Serializable {

    private String value;

    public StringPublicKey(String value) {
        this.value = value;
    }
}