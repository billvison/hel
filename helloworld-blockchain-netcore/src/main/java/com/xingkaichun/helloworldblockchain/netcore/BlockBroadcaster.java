package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NetcoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PostBlockRequest;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.*;

import java.util.List;

/**
 * 区块广播者：主动将自己最新的区块广播至全网。
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockBroadcaster {

    private NetcoreConfiguration netcoreConfiguration;
    private BlockchainCore blockchainCore;
    private NodeService nodeService;

    public BlockBroadcaster(NetcoreConfiguration netcoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netcoreConfiguration = netcoreConfiguration;
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    broadcastBlock();
                    SleepUtil.sleep(netcoreConfiguration.getBlockBroadcastTimeInterval());
                } catch (Exception e) {
                    SystemUtil.errorExit("在区块链网络中广播自己的区块出现异常",e);
                }
            }
        }).start();
    }

    private void broadcastBlock() {
        List<Node> nodes = nodeService.queryAllNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        long blockchainHeight = blockchainCore.queryBlockchainHeight();
        if(LongUtil.isLessEqualThan(blockchainHeight, GlobalSetting.GenesisBlock.HEIGHT)){
            return;
        }
        Block block = blockchainCore.queryTailBlock();
        BlockDTO blockDTO = Model2DtoTool.block2BlockDTO(block);

        //按照节点的高度进行排序
        nodes.sort((Node node1, Node node2) -> {
            if (LongUtil.isGreatThan(node1.getBlockchainHeight(), node2.getBlockchainHeight())) {
                return -1;
            } else if (LongUtil.isEquals(node1.getBlockchainHeight(), node2.getBlockchainHeight())) {
                return 0;
            } else {
                return 1;
            }
        });

        //广播节点的数量
        int broadcastNodeCount = 0;
        for(Node node:nodes){
            try {
                if(LongUtil.isLessEqualThan(blockchainHeight,node.getBlockchainHeight())){
                    continue;
                }
                PostBlockRequest postBlockRequest = new PostBlockRequest();
                postBlockRequest.setBlock(blockDTO);
                new BlockchainNodeClientImpl(node.getIp()).postBlock(postBlockRequest);
                ++broadcastNodeCount;
                if(broadcastNodeCount > 50){
                    return;
                }
                SleepUtil.sleep(1000*2);
            }catch (Exception e){
                LogUtil.error(StringUtil.format("广播区块到节点[%s]异常",node.getIp()),e);
            }
        }
    }

}
