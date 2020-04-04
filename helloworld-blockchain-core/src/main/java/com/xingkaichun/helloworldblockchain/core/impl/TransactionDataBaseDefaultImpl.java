package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.TransactionDataBase;
import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
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
