package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainNodeServerServiceImpl implements BlockchainNodeServerService {

    private BlockChainCore blockChainCore;

    public BlockchainNodeServerServiceImpl(BlockChainCore blockChainCore) {
        this.blockChainCore = blockChainCore;
    }

    @Override
    public void receiveTransaction(TransactionDTO transactionDTO) throws Exception {
        blockChainCore.getMiner().getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
    }
}
