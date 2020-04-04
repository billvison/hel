package com.xingkaichun.helloworldblockchain.node.service;


import com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.ConfigurationDto;

public interface ConfigurationService {

    /**
     * 获取矿工钱包地址
     */
    String getMinerAddress();
    /**
     * 设置矿工钱包地址
     */
    void setMinerAddress(String address);

    /**
     * 根据配置Key获取配置
     */
    ConfigurationDto getConfigurationByConfigurationKey(String confKey);

    /**
     * 设置配置
     */
    void setConfiguration(ConfigurationDto configurationDto);
}
