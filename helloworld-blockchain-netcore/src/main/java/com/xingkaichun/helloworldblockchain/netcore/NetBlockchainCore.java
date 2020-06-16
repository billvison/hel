package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.AutomaticDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.BlockchainBranchDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.netserver.HttpServer;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

public class NetBlockchainCore {

    private BlockChainCore blockChainCore;
    private AutomaticDaemonService automaticDaemonService;
    private BlockchainBranchDaemonService blockchainBranchDaemonService;
    private HttpServer httpServer;
    private ConfigurationService configurationService;

    static {
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if(provider == null){
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    public NetBlockchainCore(BlockChainCore blockChainCore, AutomaticDaemonService automaticDaemonService, BlockchainBranchDaemonService blockchainBranchDaemonService, HttpServer httpServer, ConfigurationService configurationService) {
        this.blockChainCore = blockChainCore;
        this.automaticDaemonService = automaticDaemonService;
        this.blockchainBranchDaemonService = blockchainBranchDaemonService;
        this.httpServer = httpServer;
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




    public void start() throws Exception {
        //启动本地的单机区块链
        blockChainCore.start();
        //启动区块链节点服务器
        httpServer.start();
        //同步区块链网络中的节点、区块
        automaticDaemonService.start();
        //处理本地区块链的分叉
        blockchainBranchDaemonService.start();
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

    public BlockchainBranchDaemonService getBlockchainBranchDaemonService() {
        return blockchainBranchDaemonService;
    }

    public void setBlockchainBranchDaemonService(BlockchainBranchDaemonService blockchainBranchDaemonService) {
        this.blockchainBranchDaemonService = blockchainBranchDaemonService;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }
}
