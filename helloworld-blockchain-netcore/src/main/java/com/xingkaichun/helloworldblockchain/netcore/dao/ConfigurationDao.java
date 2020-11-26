package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.entity.ConfigurationEntity;

/**
 * 配置dao
 * 读取配置，保存配置，应用启动后，利用它恢复配置。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface ConfigurationDao {

    ConfigurationEntity getConfigurationValue(String confKey) ;

    void addConfiguration(ConfigurationEntity configurationEntity) ;

    void updateConfiguration(ConfigurationEntity configurationEntity) ;
}
