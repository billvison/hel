package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
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

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * 区块广播者
 * 如果本地区块链的高度高于全网，那么就应该通知(在区块链网络中广播自己的高度)其它节点
 * ，好让其它节点知道可以来同步自己的区块数据了。
 * 至于其它节点什么时候来同步自己的区块，应该由其它节点来决定。
 *
 * 随便说一句，区块广播者会自动的将矿工挖取的区块自动的传播出去。矿工把区块放入区块链，
 * 当区块广播者广播这个区块高度时，也就完成了广播矿工挖取的区块。
 *
 * @author 邢开春 xingkaichun@qq.com
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
                    broadcastLocalBlcokChainHeight();
                } catch (Exception e) {
                    logger.error("在区块链网络中广播自己的区块高度出现异常",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();
    }


    /**
     * 发现自己的区块链高度比全网节点都要高，则广播自己的区块高度
     */
    private void broadcastLocalBlcokChainHeight() {
        List<NodeDto> nodes = nodeService.queryAllNoForkAliveNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        BigInteger localBlockChainHeight = blockChainCoreService.queryBlockChainHeight();
        boolean isLocalBlockChainHighest = true;
        for(NodeDto node:nodes){
            if(BigIntegerUtil.isLessThan(localBlockChainHeight,node.getBlockChainHeight())){
                isLocalBlockChainHighest = false;
                break;
            }
        }

        //TODO 改善型功能 性能调整 根据网络带宽设置传播宽度，这里存在可能你所发送的节点一起向你请求数据。
        //通知按照区块高度较高的先
        if(isLocalBlockChainHighest){
            //广播节点数量
            int broadcastNodeCount = 0;
            //排序节点
            Collections.sort(nodes,(NodeDto node1, NodeDto node2)->{
                if(BigIntegerUtil.isGreatThan(node1.getBlockChainHeight(),node2.getBlockChainHeight())){
                    return -1;
                } else if(node1.getBlockChainHeight() == node2.getBlockChainHeight()){
                    return 0;
                } else {
                    return 1;
                }
            });
            for(NodeDto node:nodes){
                blockchainNodeClientService.unicastLocalBlockChainHeight(node,localBlockChainHeight);
                ++broadcastNodeCount;
                if(broadcastNodeCount>20){
                    return;
                }
            }
        }
    }
}
