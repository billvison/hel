package com.xingkaichun.helloworldblockchain.core;

/**
 * 配置数据库
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class ConfigurationDatabase {

    public abstract byte[] getConfigurationValue(byte[] configurationKey) ;

    public abstract void addOrUpdateConfiguration(byte[] configurationKey,byte[] configurationValue) ;

    public abstract boolean isMinerActive();
    public abstract void activeMiner() ;
    public abstract void deactiveMiner() ;
}
