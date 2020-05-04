package com.xingkaichun.helloworldblockchain.core.model.transaction;

/**
 * 交易类型
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public enum TransactionType {

    NORMAL(1,"普通交易"),
    MINER_AWARD(2,"挖矿奖励"),
    COMMUNITY_MAINTENANCE(3,"社区维护");
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
