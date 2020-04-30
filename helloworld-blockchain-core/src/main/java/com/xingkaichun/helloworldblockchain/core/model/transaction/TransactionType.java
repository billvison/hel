package com.xingkaichun.helloworldblockchain.core.model.transaction;

/**
 * 交易类型
 */
public enum TransactionType {

    NORMAL(1,"普通交易"),
    MINER(2,"挖矿奖励交易");
    //DESTORY(3,"销毁币");//TODO 实现
    // ACROSS_CHAIN(4,"跨链交易"),//TODO 实现

    private int code;
    private String describle;

    TransactionType(int code, String describle) {
        this.code = code;
        this.describle = describle;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }
}
