package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.MineAward;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class MineAwardDefaultImpl implements MineAward {

    public BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) {
        return new BigDecimal("100");
    }
}
