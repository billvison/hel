package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PostBlockRequest;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.SleepUtil;

import java.util.List;

/**
 * 区块广播者：将区块链高度传播至全网。
 * 主动将自己最新的区块广播出去。
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockBroadcaster {

    private NodeService nodeService;
    private BlockchainCore blockchainCore;

    public BlockBroadcaster(NodeService nodeService, BlockchainCore blockchainCore) {

        this.nodeService = nodeService;
        this.blockchainCore = blockchainCore;
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    broadcastBlock();
                } catch (Exception e) {
                    LogUtil.error("在区块链网络中广播自己的区块出现异常",e);
                }
                SleepUtil.sleep(GlobalSetting.NodeConstant.BLOCK_BROADCASTER_TIME_INTERVAL);

            }
        }).start();
    }

    private void broadcastBlock() {
        List<NodeEntity> nodes = nodeService.queryAllNodeList();
        if(nodes == null || nodes.size()==0){
            return;
        }

        long blockchainHeight = blockchainCore.queryBlockchainHeight();
        Block block = blockchainCore.queryTailBlock();
        BlockDTO blockDTO = Model2DtoTool.block2BlockDTO(block);

        //按照节点的高度进行排序
        nodes.sort((NodeEntity node1, NodeEntity node2) -> {
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
        for(NodeEntity node:nodes){
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
            SleepUtil.sleep(1000*10);
        }
    }

}
