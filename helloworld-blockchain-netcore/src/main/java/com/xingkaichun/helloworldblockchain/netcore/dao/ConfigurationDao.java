package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.model.ConfigurationEntity;

/**
 * 配置dao
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface ConfigurationDao {

    String getConfiguratioValue(String confKey) ;

    void addConfiguration(ConfigurationEntity configurationEntity) ;

    void updateConfiguration(ConfigurationEntity configurationEntity) ;
}
