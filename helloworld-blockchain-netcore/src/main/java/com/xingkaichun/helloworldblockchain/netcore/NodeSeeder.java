package com.xingkaichun.helloworldblockchain.netcore;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 节点种子
 * 搜寻区块链网络中种子节点。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class NodeSeeder {

    private static final Logger logger = LoggerFactory.getLogger(NodeSeeder.class);

    private NodeService nodeService;
    private ConfigurationService configurationService;
    private Gson gson = new Gson();


    public NodeSeeder(NodeService nodeService, ConfigurationService configurationService) {
        this.nodeService = nodeService;
        this.configurationService = configurationService;
        this.gson = gson;
    }

    public void start() {
        //阻塞：将种子节点加入区块链
        addSeedNodeToLocalBlockchain();
        new Thread(()->{
            while (true){
                /**
                 * 定时将种子节点加入区块链，因为有的种子节点可能会发生故障，然后本地节点链接不上种子节点，就将种子节点丢弃。
                 * 能作为种子节点的服务器，肯定会很快被维护的。
                 */
                try {
                    addSeedNodeToLocalBlockchain();
                } catch (Exception e) {
                    logger.error("定时将种子节点加入区块链网络",e);
                }
                ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.ADD_SEED_NODE_TO_LOCAL_BLOCKCHAIN_TIME_INTERVAL.name());
                ThreadUtil.sleep(Long.parseLong(configurationDto.getConfValue()));
            }
        }).start();
    }

    private void addSeedNodeToLocalBlockchain() {
        for(String strNode: GlobalSetting.SEED_NODE_LIST){
            NodeDto node = new NodeDto();
            String[] nodeDetail = strNode.split(":");
            node.setIp(nodeDetail[0]);
            node.setPort(Integer.parseInt(nodeDetail[1]));
            NodeDto n = nodeService.queryNode(node);
            if(n == null){
                nodeService.addNode(node);
            }
        }
    }
}
