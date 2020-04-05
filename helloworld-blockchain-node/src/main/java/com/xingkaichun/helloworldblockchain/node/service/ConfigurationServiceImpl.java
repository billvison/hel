package com.xingkaichun.helloworldblockchain.node.service;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.node.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.node.model.ConfigurationEntity;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {


    @Autowired
    private ConfigurationDao configurationDao;


    private static final String MINER_ADDRESS = "MinerAddress" ;



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
    public ConfigurationDto getConfigurationByConfigurationKey(String confKey) {
        if(Strings.isNullOrEmpty(confKey)){
            return null;
        }
        ConfigurationDto configurationDto = new ConfigurationDto();
        configurationDto.setConfKey(confKey);
        String configurationValue = configurationDao.getConfiguratioValue(confKey);
        if(!Strings.isNullOrEmpty(configurationValue)){
            configurationDto.setConfValue(configurationValue);
            return configurationDto;
        }

        //默认值
        for (ConfigurationEnum configurationEnum:ConfigurationEnum.values()){
            if(configurationEnum.name().equals(confKey)){
                configurationDto.setConfValue(configurationEnum.getDefaultConfValue());
                return configurationDto;
            }
        }
        throw new RuntimeException(String.format("系统中不存在配置%s",confKey));
    }

    @Transactional
    @Override
    public void setConfiguration(ConfigurationDto configurationDto) {
        String confKey = configurationDto.getConfKey();
        String confValue = configurationDto.getConfValue();
        if(Strings.isNullOrEmpty(confValue)){
            throw new NullPointerException(confKey);
        }
        if(Strings.isNullOrEmpty(confValue)){
            throw new NullPointerException(confValue);
        }
        //检查是否存在配置
        boolean exist = false;
        for (ConfigurationEnum configurationEnum: ConfigurationEnum.values()){
            if(configurationEnum.name().equals(confKey)){
                exist = true;
            }
        }
        if(!exist){
            throw new RuntimeException(String.format("系统中不存在配置%s",confKey));
        }
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.setConfKey(confKey);
        configurationEntity.setConfValue(confValue);
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