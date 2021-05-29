package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.PostBlockRequest;
import com.xingkaichun.helloworldblockchain.setting.Setting;
import com.xingkaichun.helloworldblockchain.util.*;

import java.util.List;

/**
 * 区块广播器：主动将自己最新的区块广播至全网。
 * 别的节点可能由于这样那样的原因，不来同步我的区块，可我的这个区块对我很重要
 * ，例如我在全网前新挖了一个区块，可没人来同步我，我的区块不能传播至全网，我心有不甘呀，只有尝试将我的区块硬塞给别的节点了
 * ，如果别的节点真的是由于有'这样那样的原因'没来同步我，而不是恶意不同步我
 * ，那它接到我硬塞给它的区块，它肯定会将接收到的区块保存到自身区块链之中。
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockBroadcaster {

    private NetCoreConfiguration netCoreConfiguration;
    private BlockchainCore blockchainCore;
    private NodeService nodeService;

    public BlockBroadcaster(NetCoreConfiguration netCoreConfiguration, BlockchainCore blockchainCore, NodeService nodeService) {
        this.netCoreConfiguration = netCoreConfiguration;
        this.blockchainCore = blockchainCore;
        this.nodeService = nodeService;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    broadcastBlock();
                    SleepUtil.sleep(netCoreConfiguration.getBlockBroadcastTimeInterval());
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
        if(LongUtil.isLessEqualThan(blockchainHeight, Setting.GenesisBlockSetting.HEIGHT)){
            return;
        }
        Block block = blockchainCore.queryTailBlock();
        BlockDto blockDTO = Model2DtoTool.block2BlockDTO(block);

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
