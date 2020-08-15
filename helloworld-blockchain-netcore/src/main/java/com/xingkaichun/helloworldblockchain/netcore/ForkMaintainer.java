package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.fork.BlockchainForkDto;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainForkService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 区块链分支维护者
 * 定时检测区块链分支是否正确，如果不正确，则回滚区块，直至分支正确。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ForkMaintainer {

    private static final Logger logger = LoggerFactory.getLogger(ForkMaintainer.class);

    private BlockChainForkService blockChainForkService;
    private ConfigurationService configurationService;
    private BlockchainForkDto initBlockchainForkDto;

    public ForkMaintainer(BlockChainForkService blockChainForkService, ConfigurationService configurationService, BlockchainForkDto initBlockchainForkDto) {
        this.blockChainForkService = blockChainForkService;
        this.configurationService = configurationService;
        this.initBlockchainForkDto = initBlockchainForkDto;

        restoreConfiguration();
    }
    /**
     * 恢复配置
     */
    private void restoreConfiguration() {
        ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.IS_BLOCKCHAIN_FORK_INIT.name());
        if(configurationDto == null || !Boolean.valueOf(configurationDto.getConfValue())){
            if(initBlockchainForkDto != null){
                blockChainForkService.updateBlockchainFork(initBlockchainForkDto.getBlockList());
            }
            configurationDto = new ConfigurationDto(ConfigurationEnum.IS_BLOCKCHAIN_FORK_INIT.name(),Boolean.TRUE.toString());
            configurationService.setConfiguration(configurationDto);
        }
    }

    public void start() {
        new Thread(()->{
            while (true){
                try {
                    blockChainForkService.blockchainForkHandler();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                ThreadUtil.sleep(10*60*1000);
            }
        }).start();
    }
}
