package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.TransactionDataBase;
import com.xingkaichun.blockchain.core.dto.BlockDTO;
import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO 改善型功能 这里是个空实现
public class TransactionDataBaseDefaultImpl extends TransactionDataBase {

    private Logger logger = LoggerFactory.getLogger(TransactionDataBaseDefaultImpl.class);

    @Override
    public void insertTransaction(TransactionDTO transactionDTO) throws Exception {

    }

    @Override
    public void insertBlockDTO(BlockDTO blockDTO) throws Exception {

    }

}
