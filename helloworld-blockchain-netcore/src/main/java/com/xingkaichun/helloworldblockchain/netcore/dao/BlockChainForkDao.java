package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.model.BlockchainForkBlockEntity;

import java.util.List;

/**
 * 区块分叉dao
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface BlockChainForkDao {


    List<BlockchainForkBlockEntity> queryAllBlockchainForkBlock();

    void updateBlockchainFork(List<BlockchainForkBlockEntity> entityList);
}
