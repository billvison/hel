package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.model.Block;

public class GrowingMemoryBlockChain extends MemoryBlockChain {
    public void fillWriteBatch(Block block) throws Exception {
        fillWriteBatch(block,false);
    }
}
