package com.xingkaichun.helloworldblockchain.netcore.daemonservice;

import com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbranch.BlockchainBranchDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainBranchService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 区块链分叉处理
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainBranchDaemonService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainBranchDaemonService.class);

    private BlockChainBranchService blockChainBranchService;
    private ConfigurationService configurationService;
    private BlockchainBranchDto initBlockchainBranchDto;

    public BlockchainBranchDaemonService(BlockChainBranchService blockChainBranchService, ConfigurationService configurationService,BlockchainBranchDto initBlockchainBranchDto) throws Exception {
        this.blockChainBranchService = blockChainBranchService;
        this.configurationService = configurationService;
        this.initBlockchainBranchDto = initBlockchainBranchDto;

        init();
    }

    private void init() throws Exception {
        ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.IS_BLOCKCHAIN_BRANCH_INIT.name());
        if(configurationDto == null || !Boolean.valueOf(configurationDto.getConfValue())){
            if(initBlockchainBranchDto != null){
                blockChainBranchService.updateBranchchainBranch(initBlockchainBranchDto.getBlockList());

                configurationDto = new ConfigurationDto(ConfigurationEnum.IS_BLOCKCHAIN_BRANCH_INIT.name(),Boolean.TRUE.toString());
                configurationService.setConfiguration(configurationDto);
            }
        }
    }

    public void start() throws Exception {
        new Thread(()->{
            while (true){
                try {
                    blockChainBranchService.branchchainBranchHandler();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                try {
                    Thread.sleep(10*60*1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }
}
