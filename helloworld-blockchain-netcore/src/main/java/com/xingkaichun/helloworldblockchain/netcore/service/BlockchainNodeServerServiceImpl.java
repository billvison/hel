package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainNodeServerServiceImpl implements BlockchainNodeServerService {

    @Autowired
    private BlockChainCore blockChainCore;

    @Override
    public void receiveTransaction(TransactionDTO transactionDTO) throws Exception {
        blockChainCore.getMiner().getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
    }
}
