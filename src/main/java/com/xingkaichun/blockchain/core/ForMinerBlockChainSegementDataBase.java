package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.BlockChainSegement;

public interface ForMinerBlockChainSegementDataBase {

    boolean addBlockChainSegement(BlockChainSegement blockChainSegement) throws Exception ;

    //TODO 指定序号
    BlockChainSegement getBlockChainSegement() throws Exception ;

    void delete(BlockChainSegement blockChainSegement) throws Exception ;
}
