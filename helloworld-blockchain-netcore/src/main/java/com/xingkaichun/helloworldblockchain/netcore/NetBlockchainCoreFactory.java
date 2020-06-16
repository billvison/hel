package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.AutomaticDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.BlockchainBranchDaemonService;
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
import com.xingkaichun.helloworldblockchain.netcore.tool.InitMinerTool;
import com.xingkaichun.helloworldblockchain.netcore.util.FileUtil;

public class NetBlockchainCoreFactory {

    public static ConfigurationService configurationService = null;
    public static BlockChainCoreService blockChainCoreService = null;
    public static NodeService nodeService = null;
    public static BlockChainBranchService blockChainBranchService = null;


    public static NetBlockchainCore createNetBlcokchainCore(String dataRootPath,int serverPort) throws Exception {
        if(dataRootPath == null){
            throw new NullPointerException("参数路径不能为空。");
        }
        FileUtil.mkdir(dataRootPath);


        ConfigurationDao configurationDao = new ConfigurationDaoImpl(dataRootPath);
        configurationService = new ConfigurationServiceImpl(configurationDao);

        String minerAddress = null;
        ConfigurationDto minerAddressConfigurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.MINER_ADDRESS.name());
        if(minerAddressConfigurationDto != null){
            minerAddress = minerAddressConfigurationDto.getConfValue();
        }else {
            minerAddress = InitMinerTool.buildDefaultMinerAddress(configurationService,dataRootPath);
            configurationService.setConfiguration(new ConfigurationDto(ConfigurationEnum.MINER_ADDRESS.name(),minerAddress));
        }


        BlockChainCore blockChainCore = BlockChainCoreFactory.createBlockChainCore(dataRootPath,minerAddress);

        NodeDao nodeDao = new NodeDaoImpl(dataRootPath);
        BlockChainBranchDao blockChainBranchDao = new BlockChainBranchDaoImpl(dataRootPath);

        nodeService = new NodeServiceImpl(nodeDao,configurationService);
        BlockchainNodeClientService blockchainNodeClientService = new BlockchainNodeClientServiceImpl(serverPort,blockChainCore);
        BlockchainNodeServerService blockchainNodeServerService = new BlockchainNodeServerServiceImpl(blockChainCore);
        blockChainCoreService = new BlockChainCoreServiceImpl(blockChainCore,nodeService,blockchainNodeClientService,blockchainNodeServerService);

        blockChainBranchService = new BlockChainBranchServiceImpl(blockChainBranchDao,blockChainCoreService);
        SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService = new SynchronizeRemoteNodeBlockServiceImpl(nodeDao,blockChainCore,nodeService,blockChainBranchService,blockchainNodeClientService,configurationService);

        AutomaticDaemonService automaticDaemonService = new AutomaticDaemonService(blockChainCoreService,nodeService,synchronizeRemoteNodeBlockService,blockchainNodeClientService,blockChainCore,configurationService);
        BlockchainBranchDaemonService blockchainBranchDaemonService = new BlockchainBranchDaemonService(blockChainBranchService);

        NodeServerHandlerResolver nodeServerHandlerResolver = new NodeServerHandlerResolver(blockChainCoreService,nodeService,blockchainNodeServerService,configurationService);
        HttpServer httpServer = new HttpServer(serverPort,nodeServerHandlerResolver);
        NetBlockchainCore netBlockchainCore = new NetBlockchainCore(blockChainCore, automaticDaemonService, blockchainBranchDaemonService, httpServer, configurationService);
        return netBlockchainCore;
    }

}
