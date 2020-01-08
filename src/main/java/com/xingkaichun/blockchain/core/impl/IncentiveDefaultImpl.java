package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.Incentive;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.math.BigDecimal;

public class IncentiveDefaultImpl implements Incentive {

    @Override
    public BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) {
        return new BigDecimal("100");
    }

    @Override
    public Transaction incentiveTransaction(BlockChainDataBase blockChainDataBase, Block block) {
        return null;
    }
}
