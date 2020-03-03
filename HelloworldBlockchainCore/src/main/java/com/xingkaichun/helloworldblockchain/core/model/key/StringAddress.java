package com.xingkaichun.helloworldblockchain.core.model.key;

import lombok.Data;

/**
 * 地址
 */
@Data
public class StringAddress {

    private String value;

    public StringAddress(String value) {
        this.value = value;
    }
}
