package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.core.utils.FileUtil;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.AutomaticDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.BlockchainForkDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.dao.BlockChainForkDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.BlockChainForkDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.ConfigurationDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.NodeDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.dto.fork.BlockchainForkDto;
import com.xingkaichun.helloworldblockchain.netcore.netserver.BlockchainHttpServer;
import com.xingkaichun.helloworldblockchain.netcore.netserver.HttpServerHandlerResolver;
import com.xingkaichun.helloworldblockchain.netcore.service.*;

public class NetBlockchainCoreFactory {

    public static ConfigurationService configurationService = null;
    public static BlockChainCoreService blockChainCoreService = null;
    public static NodeService nodeService = null;
    public static BlockChainForkService blockChainForkService = null;


    public static NetBlockchainCore createNetBlcokchainCore() {
        return createNetBlcokchainCore(ResourcePathTool.getDataRootPath(),8444,null);
    }


    public static NetBlockchainCore createNetBlcokchainCore(String dataRootPath, int serverPort, BlockchainForkDto initBlockchainForkDto) {
        if(dataRootPath == null){
            throw new NullPointerException("参数路径不能为空。");
        }
        FileUtil.mkdir(dataRootPath);


        ConfigurationDao configurationDao = new ConfigurationDaoImpl(dataRootPath);
        configurationService = new ConfigurationServiceImpl(configurationDao);

        String minerAddress = configurationService.getMinerAddress();
        BlockChainCore blockChainCore = BlockChainCoreFactory.createBlockChainCore(dataRootPath,minerAddress);

        NodeDao nodeDao = new NodeDaoImpl(dataRootPath);
        BlockChainForkDao blockChainForkDao = new BlockChainForkDaoImpl(dataRootPath);

        nodeService = new NodeServiceImpl(nodeDao,configurationService);
        BlockchainNodeClientService blockchainNodeClientService = new BlockchainNodeClientServiceImpl(serverPort);
        blockChainCoreService = new BlockChainCoreServiceImpl(blockChainCore,nodeService,blockchainNodeClientService);

        blockChainForkService = new BlockChainForkServiceImpl(blockChainForkDao,blockChainCoreService);
        SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService = new SynchronizeRemoteNodeBlockServiceImpl(blockChainCore,nodeService, blockChainForkService,blockchainNodeClientService,configurationService);

        AutomaticDaemonService automaticDaemonService = new AutomaticDaemonService(blockChainCoreService,nodeService,synchronizeRemoteNodeBlockService,blockchainNodeClientService,blockChainCore,configurationService);
        BlockchainForkDaemonService blockchainForkDaemonService = new BlockchainForkDaemonService(blockChainForkService,configurationService, initBlockchainForkDto);

        HttpServerHandlerResolver httpServerHandlerResolver = new HttpServerHandlerResolver(blockChainCoreService,nodeService,configurationService);
        BlockchainHttpServer blockchainHttpServer = new BlockchainHttpServer(serverPort, httpServerHandlerResolver);
        NetBlockchainCore netBlockchainCore = new NetBlockchainCore(blockChainCore, automaticDaemonService, blockchainForkDaemonService, blockchainHttpServer, configurationService);
        return netBlockchainCore;
    }

}
