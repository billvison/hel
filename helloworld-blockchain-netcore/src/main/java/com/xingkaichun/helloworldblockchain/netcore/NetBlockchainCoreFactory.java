package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.BlockchainCoreFactory;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dao.impl.NodeDaoImpl;
import com.xingkaichun.helloworldblockchain.netcore.server.BlockchainNodeHttpServer;
import com.xingkaichun.helloworldblockchain.netcore.server.HttpServerHandlerResolver;
import com.xingkaichun.helloworldblockchain.netcore.service.NetcoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NetcoreConfigurationImpl;
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
     * @param netcorePath 区块链数据存放位置
     */
    public static NetBlockchainCore createNetBlockchainCore(String netcorePath){
        NetcoreConfiguration netcoreConfiguration = new NetcoreConfigurationImpl(netcorePath);

        String blockchainCorePath = FileUtil.newPath(netcorePath,"BlockchainCore");
        BlockchainCore blockchainCore = BlockchainCoreFactory.createBlockchainCore(blockchainCorePath);
        String slaveBlockchainCorePath = FileUtil.newPath(netcorePath,"SlaveBlockchainCore");
        BlockchainCore slaveBlockchainCore = BlockchainCoreFactory.createBlockchainCore(slaveBlockchainCorePath);


        NodeDao nodeDao = new NodeDaoImpl(netcoreConfiguration);
        NodeService nodeService = new NodeServiceImpl(nodeDao);

        HttpServerHandlerResolver httpServerHandlerResolver = new HttpServerHandlerResolver(blockchainCore,nodeService,netcoreConfiguration);
        BlockchainNodeHttpServer blockchainNodeHttpServer = new BlockchainNodeHttpServer(httpServerHandlerResolver);

        SeedNodeInitializer seedNodeInitializer = new SeedNodeInitializer(netcoreConfiguration,nodeService);
        NodeSearcher nodeSearcher = new NodeSearcher(netcoreConfiguration,nodeService);
        NodeBroadcaster nodeBroadcaster = new NodeBroadcaster(netcoreConfiguration,nodeService, blockchainCore);

        BlockchainHeightSearcher blockchainHeightSearcher = new BlockchainHeightSearcher(netcoreConfiguration,nodeService);
        BlockchainHeightBroadcaster blockchainHeightBroadcaster = new BlockchainHeightBroadcaster(netcoreConfiguration,nodeService,blockchainCore);

        BlockSearcher blockSearcher = new BlockSearcher(netcoreConfiguration,nodeService,blockchainCore, slaveBlockchainCore);
        BlockBroadcaster blockBroadcaster = new BlockBroadcaster(netcoreConfiguration,blockchainCore,nodeService);

        NetBlockchainCore netBlockchainCore
                = new NetBlockchainCore(netcoreConfiguration, blockchainCore, blockchainNodeHttpServer, nodeService
                , seedNodeInitializer, nodeSearcher, nodeBroadcaster
                , blockchainHeightSearcher, blockchainHeightBroadcaster
                , blockSearcher, blockBroadcaster
        );
        return netBlockchainCore;
    }
}
