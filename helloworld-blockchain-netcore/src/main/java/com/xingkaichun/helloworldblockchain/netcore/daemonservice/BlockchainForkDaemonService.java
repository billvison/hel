package com.xingkaichun.helloworldblockchain.netcore.daemonservice;

import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.fork.BlockchainForkDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainForkService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 区块链分叉处理
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainForkDaemonService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainForkDaemonService.class);

    private BlockChainForkService blockChainForkService;
    private ConfigurationService configurationService;
    private BlockchainForkDto initBlockchainForkDto;

    public BlockchainForkDaemonService(BlockChainForkService blockChainForkService, ConfigurationService configurationService, BlockchainForkDto initBlockchainForkDto) {
        this.blockChainForkService = blockChainForkService;
        this.configurationService = configurationService;
        this.initBlockchainForkDto = initBlockchainForkDto;

        init();
    }

    private void init() {
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
