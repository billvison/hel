package com.xingkaichun.helloworldblockchain.node.service;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.node.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.node.model.ConfigurationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {


    @Autowired
    private ConfigurationDao configurationDao;


    private static final String MINER_ADDRESS = "MinerAddress" ;
    private static final String AUTO_SEARCH_NODE_KEY = "AutoSearchNodeKey" ;
    private static final boolean DEFAULT_AUTO_SEARCH_NODE_VALUE = true ;
    private static final String NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD_KEY = "NodeErrorConnectionTimesRemoveThresholdKey" ;
    private static final long DEFAULT_NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD_VALUE = 100 ;
    private static final String NODE_SEARCH__NEW_NODE_TIME_INTERVAL_KEY = "NodeSearchNewNodeTimeIntervalKey" ;
    private static final long DEFAULT_NODE_SEARCH__NEW_NODE_TIME_INTERVAL_VALUE = 30000 ;
    private static final String SEARCH_NEW_BLOCKS_TIME_INTERVAL_KEY_KEY = "SearchNewBlocksTimeIntervalKey" ;
    private static final long DEFAULT_SEARCH_NEW_BLOCKS_TIME_INTERVAL_KEY_VALUE = 30000 ;
    private static final String CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL_KEY = "CheckLocalBlockChainHeightIsHighTimeIntervalKey" ;
    private static final long DEFAULT_CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL_VALUE = 30000 ;


    @Override
    public String getMinerAddress() {
        return configurationDao.getConfiguratioValue(MINER_ADDRESS);
    }

    @Transactional
    @Override
    public void setMinerAddress(String address) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(MINER_ADDRESS);
        configurationEntity.setConfValue(address);
        insertOrUpdate(configurationEntity);
    }

    @Override
    public boolean autoSearchNode() {
        String autoSearchNodeValue = configurationDao.getConfiguratioValue(AUTO_SEARCH_NODE_KEY);
        if(Strings.isNullOrEmpty(autoSearchNodeValue)){
            return DEFAULT_AUTO_SEARCH_NODE_VALUE;
        }
        return !Strings.isNullOrEmpty(autoSearchNodeValue) && Boolean.valueOf(autoSearchNodeValue);
    }

    @Override
    public void setAutoSearchNode(boolean autoSearchNode) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(AUTO_SEARCH_NODE_KEY);
        configurationEntity.setConfValue(String.valueOf(autoSearchNode));
        configurationDao.updateConfiguration(configurationEntity);
    }

    @Override
    public long getNodeErrorConnectionTimesRemoveThreshold() {
        String stringThreshold = configurationDao.getConfiguratioValue(NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD_KEY);
        if(Strings.isNullOrEmpty(stringThreshold)){
            return DEFAULT_NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD_VALUE;
        }
        return Long.parseLong(stringThreshold);
    }

    @Transactional
    @Override
    public void setNodeErrorConnectionTimesRemoveThreshold(long timestamp) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD_KEY);
        configurationEntity.setConfValue(String.valueOf(timestamp));
        insertOrUpdate(configurationEntity);
    }

    @Override
    public long getNodeSearchNewNodeTimeInterval() {
        String stringTimeInterval = configurationDao.getConfiguratioValue(NODE_SEARCH__NEW_NODE_TIME_INTERVAL_KEY);
        if(Strings.isNullOrEmpty(stringTimeInterval)){
            return DEFAULT_NODE_SEARCH__NEW_NODE_TIME_INTERVAL_VALUE;
        }
        return Long.parseLong(stringTimeInterval);
    }

    @Transactional
    @Override
    public void setNodeSearchNewNodeTimeInterval(long timestamp) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(NODE_SEARCH__NEW_NODE_TIME_INTERVAL_KEY);
        configurationEntity.setConfValue(String.valueOf(timestamp));
        insertOrUpdate(configurationEntity);
    }

    @Override
    public long getSearchNewBlocksTimeInterval() {
        String stringTimeInterval = configurationDao.getConfiguratioValue(SEARCH_NEW_BLOCKS_TIME_INTERVAL_KEY_KEY);
        if(Strings.isNullOrEmpty(stringTimeInterval)){
            return DEFAULT_SEARCH_NEW_BLOCKS_TIME_INTERVAL_KEY_VALUE;
        }
        return Long.parseLong(stringTimeInterval);
    }

    @Transactional
    @Override
    public void setSearchNewBlocksTimeInterval(long timestamp) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(SEARCH_NEW_BLOCKS_TIME_INTERVAL_KEY_KEY);
        configurationEntity.setConfValue(String.valueOf(timestamp));
        insertOrUpdate(configurationEntity);
    }

    @Override
    public long getCheckLocalBlockChainHeightIsHighTimeInterval() {
        String stringTimeInterval = configurationDao.getConfiguratioValue(CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL_KEY);
        if(Strings.isNullOrEmpty(stringTimeInterval)){
            return DEFAULT_CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL_VALUE;
        }
        return Long.parseLong(stringTimeInterval);
    }

    @Transactional
    @Override
    public void setCheckLocalBlockChainHeightIsHighTimeInterval(long timestamp) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL_KEY);
        configurationEntity.setConfValue(String.valueOf(timestamp));
        insertOrUpdate(configurationEntity);
    }

    void insertOrUpdate(ConfigurationEntity configurationEntity){
        String configuratioValue = configurationDao.getConfiguratioValue(configurationEntity.getConfKey());
        if(Strings.isNullOrEmpty(configuratioValue)){
            configurationDao.addConfiguration(configurationEntity);
        }else {
            configurationDao.updateConfiguration(configurationEntity);
        }
    }
}
