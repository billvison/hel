package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.BlockchainBranchBlockDto;

import java.math.BigInteger;
import java.util.List;

/**
 * 区块链分叉service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface BlockChainBranchService {

    boolean isFork(BigInteger blockHeight, String blockHash);
    BigInteger getNearBlockHeight(BigInteger blockHeight);

    boolean isBlockchainConfirmABranch();
    void updateBranchchainBranch(List<BlockchainBranchBlockDto> blockList) throws Exception;
    void branchchainBranchHandler() throws Exception;
    List<BlockchainBranchBlockDto> queryBlockchainBranch();
}
