package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;

/**
 * 监听核心区块链删除区块的动作
 */
public interface DeleteBlockActionListener {

    void deleteBlock(Block block);
}
