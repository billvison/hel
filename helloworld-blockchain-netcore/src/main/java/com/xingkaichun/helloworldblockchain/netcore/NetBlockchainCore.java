package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.netserver.BlockchainHttpServer;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;

public class NetBlockchainCore {

    private BlockChainCore blockChainCore;
    private BlockchainHttpServer blockchainHttpServer;
    private SeedNodeMaintainer seedNodeMaintainer;
    private NodeSearcher nodeSearcher;
    private BlockSearcher blockSearcher;
    private BlockBroadcaster blockBroadcaster;

    private ForkMaintainer forkMaintainer;
    private ConfigurationService configurationService;


    public NetBlockchainCore(BlockChainCore blockChainCore, ForkMaintainer forkMaintainer
            , BlockchainHttpServer blockchainHttpServer, ConfigurationService configurationService
            , SeedNodeMaintainer seedNodeMaintainer, NodeSearcher nodeSearcher
            , BlockSearcher blockSearcher , BlockBroadcaster blockBroadcaster) {

        this.blockChainCore = blockChainCore;
        this.forkMaintainer = forkMaintainer;
        this.blockchainHttpServer = blockchainHttpServer;
        this.configurationService = configurationService;
        this.seedNodeMaintainer = seedNodeMaintainer;
        this.nodeSearcher = nodeSearcher;
        this.blockSearcher = blockSearcher;
        this.blockBroadcaster = blockBroadcaster;
        restoreConfiguration();
    }

    /**
     * 恢复配置
     */
    private void restoreConfiguration() {
        //是否激活矿工
        ConfigurationDto isMinerActiveConfigurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.IS_MINER_ACTIVE.name());
        if(Boolean.valueOf(isMinerActiveConfigurationDto.getConfValue())){
            blockChainCore.getMiner().active();
        }else {
            blockChainCore.getMiner().deactive();
        }
        //是否激活同步者
        ConfigurationDto isSynchronizerActiveConfigurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.IS_SYNCHRONIZER_ACTIVE.name());
        if(Boolean.valueOf(isSynchronizerActiveConfigurationDto.getConfValue())){
            blockChainCore.getSynchronizer().active();
        }else {
            blockChainCore.getSynchronizer().deactive();
        }
    }




    public void start() {
        //启动本地的单机区块链
        blockChainCore.start();
        //启动区块链节点服务器
        blockchainHttpServer.start();

        //启动种子节点维护者
        seedNodeMaintainer.start();
        //启动节点搜寻器
        nodeSearcher.start();
        //启动区块搜寻器
        blockSearcher.start();
        //启动区块广播者
        blockBroadcaster.start();
        //启动区块链分叉维护者
        forkMaintainer.start();
    }




    //region get set
    public BlockChainCore getBlockChainCore() {
        return blockChainCore;
    }

    public BlockchainHttpServer getBlockchainHttpServer() {
        return blockchainHttpServer;
    }

    public SeedNodeMaintainer getSeedNodeMaintainer() {
        return seedNodeMaintainer;
    }

    public NodeSearcher getNodeSearcher() {
        return nodeSearcher;
    }

    public BlockSearcher getBlockSearcher() {
        return blockSearcher;
    }

    public BlockBroadcaster getBlockBroadcaster() {
        return blockBroadcaster;
    }

    public ForkMaintainer getForkMaintainer() {
        return forkMaintainer;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
    //end
}
