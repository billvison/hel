package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.utils.LongUtil;
import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockchainNodeClientService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 区块广播者：将区块链高度传播至全网。
 * 如果本地区块链的高度高于全网，那么就应该(通过在区块链网络中广播自己的高度的方式)通知其它节点
 * ，好让其它节点知道可以来同步自己的区块数据了。
 * 至于其它节点什么时候来同步自己的区块，应该由其它节点来决定。
 *
 * 随便说一句，区块广播者会自动的将矿工挖取的区块自动的传播出去。矿工把区块放入区块链后，
 * 当区块广播者广播区块链高度时，也就相当于通知其它节点自己挖出了新的区块。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockBroadcaster {

    private static final Logger logger = LoggerFactory.getLogger(BlockBroadcaster.class);

    private ConfigurationService configurationService;
    private NodeService nodeService;
    private BlockChainCoreService blockChainCoreService;
    private BlockchainNodeClientService blockchainNodeClientService;

    public BlockBroadcaster(ConfigurationService configurationService, NodeService nodeService
            , BlockChainCoreService blockChainCoreService, BlockchainNodeClientService blockchainNodeClientService) {

        this.configurationService = configurationService;
        this.nodeService = nodeService;
        this.blockChainCoreService = blockChainCoreService;
        this.blockchainNodeClientService = blockchainNodeClientService;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    broadcastBlcokChainHeight();
                } catch (Exception e) {
                    logger.error("在区块链网络中广播自己的区块高度出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();
    }


    /**
     * 发现自己的区块链高度比全网节点都要高，则广播自己的区块链高度
     */
    private void broadcastBlcokChainHeight() {
        List<NodeDto> nodes = nodeService.queryAllNoForkAliveNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        long blockChainHeight = blockChainCoreService.queryBlockChainHeight();
        //自己的高度在全网是最高的吗？是的话，传播自己的高度给其它节点，好让其它节点知道可以来同步自己的区块。
        boolean isHighest = true;
        for(NodeDto node:nodes){
            if(LongUtil.isLessThan(blockChainHeight,node.getBlockChainHeight())){
                isHighest = false;
                break;
            }
        }

        if(isHighest){
            //按照节点的高度进行排序，优先将自己的高度传播给高度大的节点。
            Collections.sort(nodes,(NodeDto node1, NodeDto node2)->{
                if(LongUtil.isGreatThan(node1.getBlockChainHeight(),node2.getBlockChainHeight())){
                    return -1;
                } else if(node1.getBlockChainHeight() == node2.getBlockChainHeight()){
                    return 0;
                } else {
                    return 1;
                }
            });
            /**
             * 用单线程轮询通知其它节点。
             * 这里可以利用多线程进行性能优化，因为本项目是helloworld项目，因此只采用单线程轮询每一个节点给它发送自己的高度，不做进一步优化拓展。
             * 这里需要考虑，如果你通知的节点立刻向你获取数据，需要考虑自己的宽带网络资源。
             * 这里采用只向部分节点发送的自己高度，且每给一个节点发送自己的高度后，睡眠几秒钟，可以认为这几秒带宽资源都分配给了这个节点。
             */
            //广播节点数量
            int broadcastNodeCount = 0;
            for(NodeDto node:nodes){
                if(LongUtil.isLessEqualThan(blockChainHeight,node.getBlockChainHeight())){
                    continue;
                }
                blockchainNodeClientService.unicastLocalBlockChainHeight(node,blockChainHeight);
                ++broadcastNodeCount;
                if(broadcastNodeCount > 20){
                    return;
                }
                ThreadUtil.sleep(1000*10);
            }
        }
    }
}
