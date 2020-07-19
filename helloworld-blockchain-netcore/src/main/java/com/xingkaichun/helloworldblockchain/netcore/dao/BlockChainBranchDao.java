package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.model.BlockchainBranchBlockEntity;

import java.util.List;

/**
 * 区块分叉dao
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface BlockChainBranchDao {


    List<BlockchainBranchBlockEntity> queryAllBlockchainBranchBlock();

    void updateBranchchainBranch(List<BlockchainBranchBlockEntity> entityList);
}
