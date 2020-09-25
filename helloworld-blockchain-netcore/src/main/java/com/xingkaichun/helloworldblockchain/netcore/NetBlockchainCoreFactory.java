package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.core.utils.FileUtil;
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

/**
 * 网络版区块链核心工厂
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class NetBlockchainCoreFactory {

    private NetBlockchainCore netBlockchainCore;
    private ConfigurationService configurationService;
    private BlockChainCoreService blockChainCoreService;
    private NodeService nodeService;
    private BlockChainForkService blockChainForkService;
    private BlockChainCore blockChainCore;

    public NetBlockchainCoreFactory(){
        this(ResourcePathTool.getDataRootPath(),8444,null);
    }

    public NetBlockchainCoreFactory(String dataRootPath, int serverPort, BlockchainForkDto initBlockchainForkDto){
        if(dataRootPath == null){
            throw new NullPointerException("参数路径不能为空。");
        }
        FileUtil.mkdir(dataRootPath);


        ConfigurationDao configurationDao = new ConfigurationDaoImpl(dataRootPath);
        configurationService = new ConfigurationServiceImpl(configurationDao);

        String minerAddress = configurationService.getMinerAddress();
        blockChainCore = BlockChainCoreFactory.createBlockChainCore(dataRootPath,minerAddress);

        NodeDao nodeDao = new NodeDaoImpl(dataRootPath);
        BlockChainForkDao blockChainForkDao = new BlockChainForkDaoImpl(dataRootPath);

        nodeService = new NodeServiceImpl(nodeDao,configurationService);
        BlockchainNodeClientService blockchainNodeClientService = new BlockchainNodeClientServiceImpl(serverPort);
        blockChainCoreService = new BlockChainCoreServiceImpl(blockChainCore,nodeService,blockchainNodeClientService);

        blockChainForkService = new BlockChainForkServiceImpl(blockChainForkDao,blockChainCoreService);
        SynchronizeRemoteNodeBlockService synchronizeRemoteNodeBlockService = new SynchronizeRemoteNodeBlockServiceImpl(blockChainCore,nodeService, blockChainForkService,blockchainNodeClientService,configurationService);

        ForkMaintainer forkMaintainer = new ForkMaintainer(blockChainForkService,configurationService, initBlockchainForkDto);

        HttpServerHandlerResolver httpServerHandlerResolver = new HttpServerHandlerResolver(blockChainCoreService,nodeService,configurationService);
        BlockchainHttpServer blockchainHttpServer = new BlockchainHttpServer(serverPort, httpServerHandlerResolver);
        SeedNodeMaintainer seedNodeMaintainer = new SeedNodeMaintainer(nodeService,configurationService);
        NodeSearcher nodeSearcher = new NodeSearcher(configurationService,nodeService,blockchainNodeClientService);
        BlockSearcher blockSearcher = new BlockSearcher(nodeService,blockChainCoreService,synchronizeRemoteNodeBlockService,blockChainCore,configurationService,blockchainNodeClientService);
        BlockBroadcaster blockBroadcaster = new BlockBroadcaster(configurationService,nodeService,blockChainCoreService,blockchainNodeClientService);


        netBlockchainCore
                = new NetBlockchainCore(blockChainCore, forkMaintainer, blockchainHttpServer, configurationService
                , seedNodeMaintainer,nodeSearcher,blockSearcher, blockBroadcaster);
    }


    public NetBlockchainCore getNetBlockchainCore() {
        return netBlockchainCore;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
    public BlockChainCoreService getBlockChainCoreService() {
        return blockChainCoreService;
    }
    public NodeService getNodeService() {
        return nodeService;
    }
    public BlockChainForkService getBlockChainForkService() {
        return blockChainForkService;
    }

    public BlockChainCore getBlockChainCore() {
        return blockChainCore;
    }
}
