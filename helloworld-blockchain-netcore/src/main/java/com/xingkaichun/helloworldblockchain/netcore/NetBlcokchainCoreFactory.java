package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.netcore.dao.BlockChainBranchDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.BlockChainBranchDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.ConfigurationDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.NodeDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.netserver.HttpServer;
import com.xingkaichun.helloworldblockchain.netcore.netserver.NodeServerHandlerResolver;
import com.xingkaichun.helloworldblockchain.netcore.service.*;
import com.xingkaichun.helloworldblockchain.netcore.timer.BlockchainBranchHandler;
import com.xingkaichun.helloworldblockchain.netcore.timer.TimerService;

public class NetBlcokchainCoreFactory {

    public static ConfigurationService configurationService = null;
    public static BlockChainCoreService blockChainCoreService = null;
    public static NodeService nodeService = null;
    public static BlockChainBranchService blockChainBranchService = null;


    public static NetBlcokchainCore createNetBlcokchainCore(String blockchainDataPath,String minerAddress) throws Exception {

        //TODO
        //InitMinerHandler initMinerHandler = new InitMinerHandler();
        //initMinerHandler.startThread();

        BlockChainCoreFactory factory = new BlockChainCoreFactory();
        BlockChainCore blockChainCore = factory.createBlockChainCore(blockchainDataPath,minerAddress);

        ConfigurationDao configurationDao = new ConfigurationDaoImpl(blockchainDataPath);
        NodeDao nodeDao = new NodeDaoImpl(blockchainDataPath);
        BlockChainBranchDao blockChainBranchDao = new BlockChainBranchDaoImpl(blockchainDataPath);

        configurationService = new ConfigurationServiceImpl(configurationDao);
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
        return netBlcokchainCore;
    }


/*    public BlockChainCore buildBlockChainCore(String blockchainDataPath) throws Exception {
        if(Strings.isNullOrEmpty(blockchainDataPath)){
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            blockchainDataPath = new File(path,"blockchaindata").getAbsolutePath();
        }
        System.out.println(String.format("区块链数据存放的路径是%s",blockchainDataPath));

        //矿工钱包地址
        ConfigurationDto minerAddressConfigurationDto =  configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.MINER_ADDRESS.name());
        BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainDataPath,minerAddressConfigurationDto.getConfValue());

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

        return blockChainCore;
    }*/
}
