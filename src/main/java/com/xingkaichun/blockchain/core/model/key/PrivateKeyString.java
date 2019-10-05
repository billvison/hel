package com.xingkaichun.blockchain.core.model.key;

import lombok.Data;

/**
 * 私钥
 */
@Data
public class PrivateKeyString {

    private String value;

    public PrivateKeyString(String value) {
        this.value = value;
    }
}
