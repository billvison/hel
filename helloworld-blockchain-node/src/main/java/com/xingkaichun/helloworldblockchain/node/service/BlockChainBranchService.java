package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.BlockchainBranchBlockDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.InitBlockHash;

import java.util.List;

public interface BlockChainBranchService {


    boolean isConfirmBlockchainBranch();

    void update(InitBlockHash initBlockHash);

    void checkBlockchainBranch() throws Exception;

    List<BlockchainBranchBlockDto> queryBlockchainBranch();

    void updateBranchchainBranch(List<BlockchainBranchBlockDto> blockList);
}
