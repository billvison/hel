package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.netcore.dao.BlockChainBranchDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.BlockChainBranchDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.ConfigurationDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.NodeDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.netserver.HttpServer;
import com.xingkaichun.helloworldblockchain.netcore.netserver.NodeServerHandlerResolver;
import com.xingkaichun.helloworldblockchain.netcore.service.*;
import com.xingkaichun.helloworldblockchain.netcore.timer.BlockchainBranchHandler;
import com.xingkaichun.helloworldblockchain.netcore.timer.InitMinerHandler;
import com.xingkaichun.helloworldblockchain.netcore.timer.TimerService;

import java.io.File;

public class NetBlcokchainCoreFactory {

    public static ConfigurationService configurationService = null;
    public static BlockChainCoreService blockChainCoreService = null;
    public static NodeService nodeService = null;
    public static BlockChainBranchService blockChainBranchService = null;


    public static NetBlcokchainCore createNetBlcokchainCore(String defaultDataRootPath,String defaultMinerAddress) throws Exception {
        ConfigurationDao configurationDao = new ConfigurationDaoImpl(defaultDataRootPath);
        configurationService = new ConfigurationServiceImpl(configurationDao);

        ConfigurationDto dataRootPathConfigurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.DATA_ROOT_PATH.name());
        if(dataRootPathConfigurationDto == null){
            defaultDataRootPath = buildDefaultDataRootPath();
            configurationService.setConfiguration(new ConfigurationDto(ConfigurationEnum.DATA_ROOT_PATH.name(),defaultDataRootPath));
        }else {
            defaultDataRootPath = dataRootPathConfigurationDto.getConfValue();
        }

        ConfigurationDto minerAddressConfigurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.MINER_ADDRESS.name());
        if(minerAddressConfigurationDto != null){
            defaultMinerAddress = dataRootPathConfigurationDto.getConfValue();
        }else {
            if(defaultMinerAddress == null){
                defaultMinerAddress = InitMinerHandler.buildDefaultMinerAddress(configurationService,defaultDataRootPath);
            }
            configurationService.setConfiguration(new ConfigurationDto(ConfigurationEnum.MINER_ADDRESS.name(),defaultMinerAddress));
        }


        BlockChainCoreFactory factory = new BlockChainCoreFactory();
        BlockChainCore blockChainCore = factory.createBlockChainCore(defaultDataRootPath,defaultMinerAddress);

        NodeDao nodeDao = new NodeDaoImpl(defaultDataRootPath);
        BlockChainBranchDao blockChainBranchDao = new BlockChainBranchDaoImpl(defaultDataRootPath);

        nodeService = new NodeServiceImpl(nodeDao,blockChainCore,configurationService);
        BlockchainNodeClientService blockchainNodeClientService = new BlockchainNodeClientServiceImpl(blockChainCore);
        BlockchainNodeServerService blockchainNodeServerService = new BlockchainNodeServerServiceImpl(blockChainCore);
        blockChainCoreService = new BlockChainCoreServiceImpl(blockChainCore,nodeService,blockchainNodeClientService,blockchainNodeServerService);

        blockChainBranchService = new BlockChainBranchServiceImpl(blockChainBranchDao,blockChainCoreService);
        SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService = new SynchronizeRemoteNodeBlockServiceImpl(nodeDao,blockChainCore,nodeService,blockChainBranchService,blockchainNodeClientService,configurationService);

        TimerService timerService = new TimerService(blockChainCoreService,nodeService,synchronizeRemoteNodeBlockService,blockchainNodeClientService,blockChainCore,configurationService);
        BlockchainBranchHandler blockchainBranchHandler = new BlockchainBranchHandler(blockChainBranchService);

        NodeServerHandlerResolver nodeServerHandlerResolver = new NodeServerHandlerResolver(blockChainCoreService,nodeService,blockchainNodeServerService,configurationService);
        HttpServer httpServer = new HttpServer(nodeServerHandlerResolver);
        NetBlcokchainCore netBlcokchainCore = new NetBlcokchainCore(blockChainCore,timerService,blockchainBranchHandler,httpServer);


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

        return netBlcokchainCore;
    }

    public static NetBlcokchainCore createNetBlcokchainCore() throws Exception {
        return createNetBlcokchainCore(null,null);
    }

    public static String buildDefaultDataRootPath() throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        return new File(path,"HelloworldBlockchainRootData").getAbsolutePath();
    }
}
