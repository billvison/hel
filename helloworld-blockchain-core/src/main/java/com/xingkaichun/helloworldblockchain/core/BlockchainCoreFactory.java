package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.impl.*;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;

/**
 * 创建BlockchainCore的工厂
 *
 * @author 邢开春 409060350@qq.com
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

        UnconfirmedTransactionDatabase unconfirmedTransactionDataBase = new UnconfirmedTransactionDatabaseDefaultImpl(blockchainDataPath);
        Wallet wallet = new WalletImpl(blockchainDataPath);
        ConfigurationDatabase configurationDatabase = new ConfigurationDatabaseDefaultImpl(blockchainDataPath);
        Miner miner = new MinerDefaultImpl(configurationDatabase,wallet,blockchainDataBase,unconfirmedTransactionDataBase);
        BlockchainCore blockchainCore = new BlockchainCoreImpl(blockchainDataBase,wallet,miner);
        return blockchainCore;
    }
}
