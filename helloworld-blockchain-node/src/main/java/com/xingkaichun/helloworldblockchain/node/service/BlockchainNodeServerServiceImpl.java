package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockchainNodeServerServiceImpl implements BlockchainNodeServerService {

    @Autowired
    private BlockChainCore blockChainCore;

    @Override
    public void receiveTransaction(TransactionDTO transactionDTO) throws Exception {
        blockChainCore.getMiner().getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
    }
}
