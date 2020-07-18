package com.xingkaichun.helloworldblockchain.netcore.service;


import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;

/**
 * 配置service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface ConfigurationService {

    /**
     * 根据配置Key获取配置
     */
    ConfigurationDto getConfigurationByConfigurationKey(String confKey);

    /**
     * 设置配置
     */
    void setConfiguration(ConfigurationDto configurationDto);

    /**
     * 获取默认矿工账户
     */
    StringAccount getDefaultMinerAccount();

    /**
     * 获取矿工钱包地址
     * 如果有用户设置矿工钱包地址，则返回用户设置的矿工钱包地址；
     * 否则，返回默认矿工账户钱包地址
     */
    String getMinerAddress();
}
