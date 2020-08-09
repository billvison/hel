package com.xingkaichun.helloworldblockchain.core.model.enums;

/**
 * 用于区分区块哈希、交易哈希、交易输出哈希的标记
 * @author 邢开春 xingkaichun@qq.com
 */
public enum HashEnum {

    BLOCK_HASH("01"),
    TRANSACTION_HASH("02"),
    TRANSACTION_OUTPUT_HASH("03");

    private String code;

    HashEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
