package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.BlockchainCoreFactory;
import com.xingkaichun.helloworldblockchain.core.ConfigurationDatabase;
import com.xingkaichun.helloworldblockchain.core.impl.ConfigurationDatabaseDefaultImpl;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.NodeDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.server.BlockchainNodeHttpServer;
import com.xingkaichun.helloworldblockchain.netcore.server.HttpServerHandlerResolver;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationServiceImpl;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeServiceImpl;
import com.xingkaichun.helloworldblockchain.util.FileUtil;

/**
 * 网络版区块链核心工厂
 *
 * @author 邢开春 409060350@qq.com
 */
public class NetBlockchainCoreFactory {

    /**
     * 创建NetBlockchainCore实例
     */
    public static NetBlockchainCore createNetBlockchainCore(){
        return createNetBlockchainCore(ResourcePathTool.getDataRootPath());
    }

    /**
     * 创建NetBlockchainCore实例
     *
     * @param netBlockchainCoreDataRootPath 区块链数据存放位置
     */
    public static NetBlockchainCore createNetBlockchainCore(String netBlockchainCoreDataRootPath){
        if(netBlockchainCoreDataRootPath == null){
            throw new NullPointerException("参数路径不能为空。");
        }
        FileUtil.mkdirs(netBlockchainCoreDataRootPath);
        String blockchainCoreDataRootPath = FileUtil.newPath(netBlockchainCoreDataRootPath,"BlockchainCore");
        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(blockchainCoreDataRootPath);
        String slaveBlockchainCoreDataRootPath = FileUtil.newPath(netBlockchainCoreDataRootPath,"SlaveBlockchainCore");
        BlockchainCore slaveBlockchainCore = BlockchainCoreFactory.createBlockchainCore(slaveBlockchainCoreDataRootPath);

        ConfigurationDatabase configurationDatabase = new ConfigurationDatabaseDefaultImpl(netBlockchainCoreDataRootPath);
        ConfigurationService configurationService = new ConfigurationServiceImpl(configurationDatabase);

        NodeDao nodeDao = new NodeDaoImpl(netBlockchainCoreDataRootPath);
        NodeService nodeService = new NodeServiceImpl(nodeDao);

        HttpServerHandlerResolver httpServerHandlerResolver = new HttpServerHandlerResolver(blockchainCore,nodeService,configurationService);
        BlockchainNodeHttpServer blockchainNodeHttpServer = new BlockchainNodeHttpServer(httpServerHandlerResolver);
        NodeSearcher nodeSearcher = new NodeSearcher(configurationService,nodeService);
        SeedNodeInitializer seedNodeInitializer = new SeedNodeInitializer(configurationService,nodeService);
        NodeBroadcaster nodeBroadcaster = new NodeBroadcaster(nodeService, blockchainCore);
        BlockSearcher blockSearcher = new BlockSearcher(configurationService,nodeService,blockchainCore, slaveBlockchainCore);
        BlockBroadcaster blockBroadcaster = new BlockBroadcaster(nodeService,blockchainCore);
        BlockchainHeightBroadcaster blockchainHeightBroadcaster = new BlockchainHeightBroadcaster(nodeService,blockchainCore);
        BlockchainHeightSearcher blockchainHeightSearcher = new BlockchainHeightSearcher(configurationService,nodeService);

        NetBlockchainCore netBlockchainCore
                = new NetBlockchainCore(blockchainCore, blockchainNodeHttpServer, configurationService, nodeService
                , seedNodeInitializer, nodeBroadcaster, nodeSearcher
                , blockchainHeightBroadcaster, blockchainHeightSearcher
                , blockBroadcaster, blockSearcher
        );
        return netBlockchainCore;
    }
}
