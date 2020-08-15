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
 * 种子节点维护者
 * 区块链系统将所知道的所有节点都存入到数据库。数据库的保存的所有节点称为节点列表。
 * 保证种子节点始终在节点列表中。
 * 种子节点也可能由于维护等原因，暂时不可以访问。由于种子节点不可用，就会被区块链系统从节点列表里给丢弃了。因此这里
 * ，每隔一定的时间重新将种子节点加入到节点列表。无论如何，每隔一定时间将种子节点重新加入节点列表总没问题。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SeedNodeMaintainer {

    private static final Logger logger = LoggerFactory.getLogger(SeedNodeMaintainer.class);

    private NodeService nodeService;
    private ConfigurationService configurationService;
    private Gson gson = new Gson();


    public SeedNodeMaintainer(NodeService nodeService, ConfigurationService configurationService) {
        this.nodeService = nodeService;
        this.configurationService = configurationService;
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
