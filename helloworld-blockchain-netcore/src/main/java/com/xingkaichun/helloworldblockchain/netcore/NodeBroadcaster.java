package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.node.client.BlockchainNodeClient;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 节点广播器
 * 在区块链网络中，广播自己。
 * 例如，系统启动后，广播自己，让区块链网络的其它节点知道自己已经上线了。
 * 再例如，由于未知原因，部分节点与自己中断了联系，自己已经不在它们的节点列表中了，
 * 而自己的列表中有它们，这时候可以广播一下自己，这些中断联系的节点将会恢复与自己联系。
 * @author 邢开春
 */
public class NodeBroadcaster {

    private static final Logger logger = LoggerFactory.getLogger(NodeBroadcaster.class);

    private NodeService nodeService;
    private BlockchainNodeClient blockchainNodeClient;

    public NodeBroadcaster(NodeService nodeService
            , BlockchainNodeClient blockchainNodeClient) {

        this.nodeService = nodeService;
        this.blockchainNodeClient = blockchainNodeClient;
    }

    public void start() {
        new Thread(()->{
            //定时广告自己
            while (true){
                try {
                    broadcastMyself();
                } catch (Exception e) {
                    logger.error("在区块链网络中广播自己出现异常",e);
                }
                ThreadUtil.sleep(GlobalSetting.NodeConstant.NODE_BROADCAST_TIME_INTERVAL);
            }
        }).start();
    }

    /**
     * 广播自己
     */
    private void broadcastMyself() {
        List<NodeDto> nodes = nodeService.queryAllNodeList();
        for(NodeDto node:nodes){
            blockchainNodeClient.pingNode(node);
        }
    }
}
