package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.impl.*;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;

/**
 * 创建BlockchainCore的工厂
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockchainCoreFactory {

    /**
     * 创建BlockchainCore实例
     */
    public static BlockchainCore createBlockchainCore(){
        return createBlockchainCore(ResourcePathTool.getDataRootPath());
    }

    /**
     * 创建BlockchainCore实例
     *
     * @param blockchainDataPath 区块链数据存放位置
     */
    public static BlockchainCore createBlockchainCore(String blockchainDataPath) {

        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensusImpl();
        BlockchainDatabase blockchainDataBase = new BlockchainDatabaseDefaultImpl(blockchainDataPath,incentive,consensus);

        MinerTransactionDtoDatabase minerTransactionDtoDataBase = new MinerTransactionDtoDtoDatabaseDefaultImpl(blockchainDataPath);
        Wallet wallet = new WalletImpl(blockchainDataPath);
        Miner miner = new MinerDefaultImpl(wallet,blockchainDataBase,minerTransactionDtoDataBase);
        BlockchainCore blockchainCore = new BlockchainCoreImpl(blockchainDataBase,wallet,miner);
        return blockchainCore;
    }
}
