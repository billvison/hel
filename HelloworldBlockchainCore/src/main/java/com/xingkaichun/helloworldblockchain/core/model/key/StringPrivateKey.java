package com.xingkaichun.helloworldblockchain.core.model.key;

import lombok.Data;

/**
 * 私钥
 */
@Data
public class StringPrivateKey {

    private String value;

    public StringPrivateKey(String value) {
        this.value = value;
    }
}
