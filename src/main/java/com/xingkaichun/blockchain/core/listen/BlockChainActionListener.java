package com.xingkaichun.blockchain.core.listen;

import java.util.List;

/**
 * 监听核心区块链区块增、删的动作
 */
public interface BlockChainActionListener {

    void addOrDeleteBlock(List<BlockChainActionData> blockChainActionDataList);
}
