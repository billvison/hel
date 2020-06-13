package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbranch.BlockchainBranchBlockDto;

import java.math.BigInteger;
import java.util.List;

/**
 * 区块链分叉service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface BlockChainBranchService {

    boolean isFork(BigInteger blockHeight, String blockHash);

    /**
     * 获取固定Hash的[小于传入的区块高度]的最大区块高度
     * @param blockHeight 传入的区块高度
     */
    BigInteger getFixBlockHashMaxBlockHeight(BigInteger blockHeight);

    boolean isBlockchainConfirmABranch() throws Exception;
    void updateBranchchainBranch(List<BlockchainBranchBlockDto> blockList) throws Exception;
    void branchchainBranchHandler() throws Exception;
    List<BlockchainBranchBlockDto> queryBlockchainBranch() throws Exception;
}
