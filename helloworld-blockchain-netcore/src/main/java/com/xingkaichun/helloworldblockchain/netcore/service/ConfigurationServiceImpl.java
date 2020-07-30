package com.xingkaichun.helloworldblockchain.netcore.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.model.ConfigurationEntity;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ConfigurationServiceImpl implements ConfigurationService {

    private ConfigurationDao configurationDao;

    public ConfigurationServiceImpl(ConfigurationDao configurationDao) {
        this.configurationDao = configurationDao;
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

    //事务
    @Override
    public void setConfiguration(ConfigurationDto configurationDto) {
        String confKey = configurationDto.getConfKey();
        String confValue = configurationDto.getConfValue();
        if(Strings.isNullOrEmpty(confKey)){
            throw new NullPointerException("ConfKey不能为空");
        }
        if(Strings.isNullOrEmpty(confValue)){
            throw new NullPointerException("ConfValue不能为空");
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


    @Override
    public StringAccount getDefaultMinerAccount() {
        synchronized (this.getClass()){
            Gson gson = new Gson();
            ConfigurationDto configurationDto = getConfigurationByConfigurationKey(ConfigurationEnum.DEFAULT_MINER_ACCOUNT.name());
            if(configurationDto != null && !Strings.isNullOrEmpty(configurationDto.getConfValue())){
                StringAccount stringAccount = gson.fromJson(configurationDto.getConfValue(),StringAccount.class);
                return stringAccount;
            }
            StringAccount defaultStringAccount = AccountUtil.randomStringAccount();
            ConfigurationDto defaultStringAccountConfigurationDto =
                    new ConfigurationDto(ConfigurationEnum.DEFAULT_MINER_ACCOUNT.name(),gson.toJson(defaultStringAccount));
            setConfiguration(defaultStringAccountConfigurationDto);
            return defaultStringAccount;
        }
    }

    @Override
    public String getMinerAddress() {
        String minerAddress = null;
        ConfigurationDto minerAddressConfigurationDto = getConfigurationByConfigurationKey(ConfigurationEnum.MINER_ADDRESS.name());
        if(minerAddressConfigurationDto != null && !Strings.isNullOrEmpty(minerAddressConfigurationDto.getConfValue())){
            minerAddress = minerAddressConfigurationDto.getConfValue();
        }else {
            StringAccount stringAccount = getDefaultMinerAccount();
            minerAddress = stringAccount.getAddress();
        }
        return minerAddress;
    }
}