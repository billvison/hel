package com.xingkaichun.helloworldblockchain.core.model.transaction;

/**
 * 交易类型
 * 交易有且只有两种类型：创世交易、标准交易，过去、现在、未来都是如此，请不要增加、减少交易类型。
 *
 * @author 邢开春 409060350@qq.com
 */
public enum TransactionType {

    /**
     * 创世交易：每个区块的第一笔交易都有一个名字叫创世交易。每个区块有且只有一笔创世交易。
     * 在区块中，创世交易是区块的第一笔交易，没有第一笔交易，就没有其它交易。相对这个区块的其它交易来说，它是一笔极其特殊的交易
     * ，它没有交易输入，它的交易输入被允许小于交易输出，它的交易输出被用于发放矿工挖矿激励，它是维持区块链网络存在的动力之一，
     * ，所以它值得被赋予一个自己名字，与其余的标准交易作以区分，我称呼它为创世交易。
     */
    GENESIS_TRANSACTION,
    /**
     * 标准交易
     */
    STANDARD_TRANSACTION
}
