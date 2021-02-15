package com.xingkaichun.helloworldblockchain.core.model.transaction;

/**
 * 交易类型
 *
 * @author 邢开春 409060350@qq.com
 */
public enum TransactionType {

    /**
     * 普通交易
     */
    NORMAL,
    /**
     * COINBASE交易
     * 相关拓展：COINBASE是什么？https://zhuanlan.zhihu.com/p/258952493
     */
    COINBASE

}
