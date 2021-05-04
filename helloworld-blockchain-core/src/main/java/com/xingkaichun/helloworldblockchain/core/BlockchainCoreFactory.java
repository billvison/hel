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
     * @param corePath BlockchainCore数据存放位置
     */
    public static BlockchainCore createBlockchainCore(String corePath) {

        CoreConfiguration coreConfiguration = new CoreConfigurationDefaultImpl(corePath);
        Incentive incentive = new IncentiveDefaultImpl();
        Consensus consensus = new ProofOfWorkConsensusImpl();
        BlockchainDatabase blockchainDataBase = new BlockchainDatabaseDefaultImpl(coreConfiguration,incentive,consensus);

        UnconfirmedTransactionDatabase unconfirmedTransactionDataBase = new UnconfirmedTransactionDatabaseDefaultImpl(coreConfiguration);
        Wallet wallet = new WalletImpl(coreConfiguration);
        Miner miner = new MinerDefaultImpl(coreConfiguration,wallet,blockchainDataBase,unconfirmedTransactionDataBase);
        BlockchainCore blockchainCore = new BlockchainCoreImpl(coreConfiguration,blockchainDataBase,wallet,miner);
        return blockchainCore;
    }
}
