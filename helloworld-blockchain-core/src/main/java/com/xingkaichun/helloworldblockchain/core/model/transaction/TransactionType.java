package com.xingkaichun.helloworldblockchain.core.model.transaction;

/**
 * 交易类型
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public enum TransactionType {

    NORMAL(1,"普通交易"),
    //TODO 介绍
    COINBASE(2,"挖矿奖励");

    private int code;
    private String describle;

    TransactionType(int code, String describle) {
        this.code = code;
        this.describle = describle;
    }

    public int getCode() {
        return code;
    }

    public String getDescrible() {
        return describle;
    }
}
