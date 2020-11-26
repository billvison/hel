package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.entity.ConfigurationEntity;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    //矿工是否处于激活状态？
    private static final String IS_MINER_ACTIVE = "IS_MINER_ACTIVE";
    //同步者是否处于激活状态？
    private static final String IS_SYNCHRONIZER_ACTIVE = "IS_SYNCHRONIZER_ACTIVE";
    //是否自动搜寻区块链网络节点？
    private static final String IS_AUTO_SEARCH_NODE = "IS_AUTO_SEARCH_NODE";

    private BlockchainCore blockchainCore;
    private ConfigurationDao configurationDao;

    public ConfigurationServiceImpl(BlockchainCore blockchainCore,ConfigurationDao configurationDao) {
        this.blockchainCore = blockchainCore;
        this.configurationDao = configurationDao;
    }

    private void setConfiguration(ConfigurationEntity configurationEntity) {
        ConfigurationEntity configurationEntityInDb = configurationDao.getConfigurationValue(configurationEntity.getConfKey());
        if(configurationEntityInDb == null){
            configurationDao.addConfiguration(configurationEntity);
        }else {
            configurationDao.updateConfiguration(configurationEntity);
        }
    }

    @Override
    public void restoreMinerConfiguration() {
        if(isMinerActive()){
            blockchainCore.getMiner().active();
        }else {
            blockchainCore.getMiner().deactive();
        }
    }

    @Override
    public boolean isMinerActive() {
        ConfigurationEntity configurationEntity = configurationDao.getConfigurationValue(IS_MINER_ACTIVE);
        if(configurationEntity == null){
            //默认值
            return false;
        }
        return Boolean.valueOf(configurationEntity.getConfValue());
    }

    @Override
    public void activeMiner() {
        blockchainCore.getMiner().active();
        ConfigurationEntity configurationEntity = new ConfigurationEntity(IS_MINER_ACTIVE,String.valueOf(true));
        setConfiguration(configurationEntity);
    }

    @Override
    public void deactiveMiner() {
        blockchainCore.getMiner().deactive();
        ConfigurationEntity configurationEntity = new ConfigurationEntity(IS_MINER_ACTIVE,String.valueOf(false));
        setConfiguration(configurationEntity);
    }

    @Override
    public void restoreSynchronizerConfiguration() {
        if(isSynchronizerActive()){
            blockchainCore.getSynchronizer().active();
        }else {
            blockchainCore.getSynchronizer().deactive();
        }
    }

    @Override
    public boolean isSynchronizerActive() {
        ConfigurationEntity configurationEntity = configurationDao.getConfigurationValue(IS_SYNCHRONIZER_ACTIVE);
        if(configurationEntity == null){
            //默认值
            return false;
        }
        return Boolean.valueOf(configurationEntity.getConfValue());
    }

    @Override
    public void activeSynchronizer() {
        blockchainCore.getSynchronizer().active();
        ConfigurationEntity configurationEntity = new ConfigurationEntity(IS_SYNCHRONIZER_ACTIVE,String.valueOf(true));
        setConfiguration(configurationEntity);
    }

    @Override
    public void deactiveSynchronizer() {
        blockchainCore.getSynchronizer().active();
        ConfigurationEntity configurationEntity = new ConfigurationEntity(IS_SYNCHRONIZER_ACTIVE,String.valueOf(false));
        setConfiguration(configurationEntity);
    }

    @Override
    public boolean isAutoSearchNode() {
        ConfigurationEntity configurationEntity = configurationDao.getConfigurationValue(IS_AUTO_SEARCH_NODE);
        if(configurationEntity == null){
            //默认值
            return true;
        }
        return Boolean.valueOf(configurationEntity.getConfValue());
    }

    @Override
    public void setAutoSearchNode(boolean autoSearchNode) {
        ConfigurationEntity configurationDto = new ConfigurationEntity(IS_AUTO_SEARCH_NODE,String.valueOf(autoSearchNode));
        setConfiguration(configurationDto);
    }
}