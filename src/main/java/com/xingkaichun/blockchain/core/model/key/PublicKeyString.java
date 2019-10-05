package com.xingkaichun.blockchain.core.model.key;

import lombok.Data;

import java.io.Serializable;

/**
 * 公钥
 */
@Data
public class PublicKeyString implements Serializable {

    private String value;

    public PublicKeyString(String value) {
        this.value = value;
    }
}