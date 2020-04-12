package com.xingkaichun.helloworldblockchain.core.model.transaction;

/**
 * 交易类型
 */
public enum TransactionType {

    NORMAL,//普通交易
    //TODO 挖矿交易默认排在区块的最末尾 为了方便
    MINER//挖矿奖励交易
}
