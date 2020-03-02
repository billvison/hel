package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
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
