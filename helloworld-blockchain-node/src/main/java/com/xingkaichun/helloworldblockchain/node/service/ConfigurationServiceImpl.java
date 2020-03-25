package com.xingkaichun.helloworldblockchain.node.service;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.node.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.node.model.ConfigurationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    private ConfigurationDao configurationDao;

    private static final String MINER_ADDRESS = "MinerAddress" ;
    private static final String AUTO_SEARCH_NODE = "AutoSearchNode" ;

    @Override
    public String getMinerAddress() {
        return configurationDao.getConfiguratioValue(MINER_ADDRESS);
    }

    @Override
    public void writeMinerAddress(String address) {
        boolean hasMinerAddress = Strings.isNullOrEmpty(getMinerAddress());
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(MINER_ADDRESS);
        configurationEntity.setConfValue(address);
        if(!hasMinerAddress){
            configurationDao.updateConfiguration(configurationEntity);
        }else {
            configurationDao.addConfiguration(configurationEntity);
        }
    }

    @Override
    public boolean autoSearchNode() {
        String autoSearchNodeValue = configurationDao.getConfiguratioValue(AUTO_SEARCH_NODE);
        return !Strings.isNullOrEmpty(autoSearchNodeValue) && Boolean.valueOf(autoSearchNodeValue);
    }

    @Override
    public void writeAutoSearchNode(boolean autoSearchNode) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(AUTO_SEARCH_NODE);
        configurationEntity.setConfValue(String.valueOf(autoSearchNode));
        configurationDao.updateConfiguration(configurationEntity);
    }
}
