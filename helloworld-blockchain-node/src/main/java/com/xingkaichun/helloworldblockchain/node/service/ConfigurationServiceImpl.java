package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.node.model.ConfigurationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    private ConfigurationDao configurationDao;

    private static final String MINER_ADDRESS = "MinerAddress" ;

    @Override
    public String getMinerAddress() {
        return configurationDao.getConfiguratioValue(MINER_ADDRESS);
    }

    @Override
    public void writeMinerAddress(String address) {
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(MINER_ADDRESS);
        configurationEntity.setConfValue(address);
        configurationDao.addConfiguration(configurationEntity);
    }
}
