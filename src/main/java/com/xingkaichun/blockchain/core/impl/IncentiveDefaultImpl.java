package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.Incentive;
import com.xingkaichun.blockchain.core.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class IncentiveDefaultImpl extends Incentive {

    private Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) {
        return new BigDecimal("100");
    }
}
