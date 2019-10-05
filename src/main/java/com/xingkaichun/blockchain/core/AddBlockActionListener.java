package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

/**
 * 监听核心区块链增加区块的动作
 */
public interface AddBlockActionListener {

    void addBlock(Block block);
}
