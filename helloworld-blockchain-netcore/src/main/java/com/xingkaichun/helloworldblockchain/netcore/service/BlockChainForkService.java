package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.netcore.dto.fork.BlockchainForkBlockDto;

import java.util.List;

/**
 * 区块链分叉service
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface BlockChainForkService {

    boolean isFork(long blockHeight, String blockHash);

    /**
     * 获取固定Hash的[小于传入的区块高度]的最大区块高度
     */
    long getFixBlockHashMaxBlockHeight(long blockHeight);

    void updateBlockchainFork(List<BlockchainForkBlockDto> blockList) ;
    void blockchainForkHandler() ;
    List<BlockchainForkBlockDto> queryBlockchainFork() ;
}
