package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbranch.BlockchainBranchBlockDto;

import java.math.BigInteger;
import java.util.List;

public interface BlockChainBranchService {

    boolean isFork(BigInteger blockHeight, String blockHash);
    BigInteger getNearBlockHeight(BigInteger blockHeight);

    boolean isBlockchainConfirmABranch();
    void updateBranchchainBranch(List<BlockchainBranchBlockDto> blockList) throws Exception;
    void branchchainBranchHandler() throws Exception;
    List<BlockchainBranchBlockDto> queryBlockchainBranch();
}
