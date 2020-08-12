package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.AutomaticDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.BlockchainForkDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.netserver.BlockchainHttpServer;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;

public class NetBlockchainCore {

    private BlockChainCore blockChainCore;
    private AutomaticDaemonService automaticDaemonService;
    private BlockchainForkDaemonService blockchainForkDaemonService;
    private BlockchainHttpServer blockchainHttpServer;
    private ConfigurationService configurationService;

    public NetBlockchainCore(BlockChainCore blockChainCore, AutomaticDaemonService automaticDaemonService, BlockchainForkDaemonService blockchainForkDaemonService, BlockchainHttpServer blockchainHttpServer, ConfigurationService configurationService) {
        this.blockChainCore = blockChainCore;
        this.automaticDaemonService = automaticDaemonService;
        this.blockchainForkDaemonService = blockchainForkDaemonService;
        this.blockchainHttpServer = blockchainHttpServer;
        this.configurationService = configurationService;
        init();
    }

    public void init() {
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
        //同步区块链网络中的节点、区块
        automaticDaemonService.start();
        //处理本地区块链的分叉
        blockchainForkDaemonService.start();
    }




    public BlockChainCore getBlockChainCore() {
        return blockChainCore;
    }

    public void setBlockChainCore(BlockChainCore blockChainCore) {
        this.blockChainCore = blockChainCore;
    }

    public AutomaticDaemonService getAutomaticDaemonService() {
        return automaticDaemonService;
    }

    public void setAutomaticDaemonService(AutomaticDaemonService automaticDaemonService) {
        this.automaticDaemonService = automaticDaemonService;
    }

    public BlockchainForkDaemonService getBlockchainForkDaemonService() {
        return blockchainForkDaemonService;
    }

    public void setBlockchainForkDaemonService(BlockchainForkDaemonService blockchainForkDaemonService) {
        this.blockchainForkDaemonService = blockchainForkDaemonService;
    }

    public BlockchainHttpServer getBlockchainHttpServer() {
        return blockchainHttpServer;
    }

    public void setBlockchainHttpServer(BlockchainHttpServer blockchainHttpServer) {
        this.blockchainHttpServer = blockchainHttpServer;
    }
}
