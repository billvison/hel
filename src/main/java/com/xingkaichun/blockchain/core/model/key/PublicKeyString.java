package com.xingkaichun.blockchain.core.model.key;

import lombok.Data;

import java.io.Serializable;

/**
 * 公钥
 */
@Data
public class PublicKeyString implements Serializable {

    private String value;
    //TODO 钱包地址 公钥hash 校验 版本


    public PublicKeyString(String value) {
        this.value = value;
    }
}