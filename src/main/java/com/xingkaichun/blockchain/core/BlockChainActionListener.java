package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

import java.util.List;

/**
 * 监听核心区块链增加区块的动作
 */
public interface BlockChainActionListener {

    void addOrDeleteBlock(Block block, boolean addBlock, boolean deleteBlock);
}
