package com.xingkaichun.blockchain.core;


public class Boot {


    public static void main(String[] args) throws Exception {
        BlockChainCoreFactory blockChainCoreFactory = new BlockChainCoreFactory();
        BlockChainCore blockChainCore = blockChainCoreFactory.createBlockChainCore();
        blockChainCore.run();
    }
}
