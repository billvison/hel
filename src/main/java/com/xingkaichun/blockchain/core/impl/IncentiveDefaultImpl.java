package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.Incentive;
import com.xingkaichun.blockchain.core.model.Block;

import java.math.BigDecimal;

public class IncentiveDefaultImpl extends Incentive {

    @Override
    public BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) {
        return new BigDecimal("100");
    }
}
