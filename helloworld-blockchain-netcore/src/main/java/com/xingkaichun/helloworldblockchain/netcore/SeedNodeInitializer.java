package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SleepUtil;


/**
 * 种子节点初始化器
 *
 * @author 邢开春 409060350@qq.com
 */
public class SeedNodeInitializer {

    private ConfigurationService configurationService;
    private NodeService nodeService;

    public SeedNodeInitializer(ConfigurationService configurationService, NodeService nodeService) {

        this.configurationService = configurationService;
        this.nodeService = nodeService;
    }

    public void start() {
        /*
         * 定时循环的将种子节点加入区块链系统。
         * 因为有的种子节点可能会发生故障，然后本地节点链接不上种子节点，就将种子节点丢弃。
         * 能作为种子节点的服务器，肯定会很快被修复正常的。所以定时循环的将种子节点加入区块链，保证与种子节点连接是通畅的。
         */
        new Thread(()->{
            while (true){
                try {
                    if(configurationService.isAutoSearchNode()){
                        addSeedNode();
                    }
                } catch (Exception e) {
                    LogUtil.error("定时将种子节点加入区块链网络出现异常",e);
                }
                SleepUtil.sleep(GlobalSetting.NodeConstant.ADD_SEED_NODE_TIME_INTERVAL);
            }
        }).start();
    }

    /**
     * 添加种子节点
     */
    private void addSeedNode() {
        for(String nodeIp: GlobalSetting.SEED_NODE_LIST){
            NodeEntity node = new NodeEntity();
            node.setIp(nodeIp);
            NodeEntity nodeBO = nodeService.queryNode(node.getIp());
            if(nodeBO == null){
                if(configurationService.isAutoSearchNode()){
                    nodeService.addNode(node);
                }
            }
        }
    }
}
