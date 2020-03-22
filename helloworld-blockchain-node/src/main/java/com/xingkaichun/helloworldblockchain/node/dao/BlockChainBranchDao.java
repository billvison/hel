package com.xingkaichun.helloworldblockchain.node.dao;

import com.xingkaichun.helloworldblockchain.node.model.BlockchainBranchBlockEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface BlockChainBranchDao {


    List<BlockchainBranchBlockEntity> queryAllBlockchainBranchBlock();

    void removeAll();

    void add(BlockchainBranchBlockEntity entity);
}
