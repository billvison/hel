package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.impl.*;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;

import java.io.File;
import java.util.ArrayList;

/**
 * 创建BlockChainCore的工厂
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainCoreFactory {


    /**
     * 创建BlockChainCore实例
     *
     * @param blockchainDataPath 区块链数据存放位置
     * @param minerAddress 矿工钱包地址
     * @author 邢开春 xingkaichun@qq.com
     */
    public BlockChainCore createBlockChainCore(String blockchainDataPath,String minerAddress) throws Exception {

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensus();
        BlockChainDataBase blockChainDataBase = new BlockChainDataBaseDefaultImpl(blockchainDataPath,incentive,consensus);

        MinerTransactionDtoDataBase minerTransactionDtoDataBase = new MinerTransactionDtoDtoDataBaseDefaultImpl(blockchainDataPath);
        StringAddress minerStringAddress = new StringAddress(minerAddress);
        Miner miner = new MinerDefaultImpl(blockChainDataBase, minerTransactionDtoDataBase,minerStringAddress);

        SynchronizerDataBase synchronizerDataBase = new SynchronizerDataBaseDefaultImpl(blockchainDataPath);
        BlockChainDataBase temporaryBlockChainDataBase = new BlockChainDataBaseDefaultImpl(new File(blockchainDataPath,"TemporaryBlockChainDataBase").getAbsolutePath(),incentive,consensus);
        Synchronizer synchronizer = new SynchronizerDefaultImpl(blockChainDataBase,temporaryBlockChainDataBase, synchronizerDataBase);

        BlockChainCore blockChainCore = new BlockChainCoreImpl();
        blockChainCore.setMiner(miner);
        blockChainCore.setSynchronizer(synchronizer);
        blockChainCore.setBlockChainDataBase(blockChainDataBase);
        return blockChainCore;
    }
}
