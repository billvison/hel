package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.ConfigurationDatabase;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class ConfigurationServiceImpl implements ConfigurationService {

    //同步器'同步器是否是激活状态'状态存入到数据库时的主键
    private static final String SYNCHRONIZER_OPTION_KEY = "IS_SYNCHRONIZER_ACTIVE";
    //同步器'同步器是否是激活状态'开关的默认状态
    private static final boolean SYNCHRONIZER_OPTION_DEFAULT_VALUE = false;

    //节点搜索器'是否自动搜索节点'状态存入到数据库时的主键
    private static final String AUTO_SEARCH_NODE_OPTION_KEY = "IS_AUTO_SEARCH_NODE";
    //节点搜索器'是否自动搜索节点'开关的默认状态
    private static final boolean SAUTO_SEARCH_NODE_OPTION_DEFAULT_VALUE = false;

    private ConfigurationDatabase configurationDatabase;

    public ConfigurationServiceImpl(ConfigurationDatabase configurationDatabase) {
        this.configurationDatabase = configurationDatabase;
    }

    @Override
    public boolean isSynchronizerActive() {
        byte[] bytesConfigurationValue = configurationDatabase.getConfigurationValue(ByteUtil.encode(SYNCHRONIZER_OPTION_KEY));
        if(bytesConfigurationValue == null){
            return SYNCHRONIZER_OPTION_DEFAULT_VALUE;
        }
        return Boolean.valueOf(ByteUtil.decodeToUtf8String(bytesConfigurationValue));
    }

    @Override
    public void activeSynchronizer() {
        configurationDatabase.addOrUpdateConfiguration(ByteUtil.encode(SYNCHRONIZER_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.TRUE)));
    }

    @Override
    public void deactiveSynchronizer() {
        configurationDatabase.addOrUpdateConfiguration(ByteUtil.encode(SYNCHRONIZER_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.FALSE)));
    }

    @Override
    public boolean isAutoSearchNode() {
        byte[] bytesConfigurationValue = configurationDatabase.getConfigurationValue(ByteUtil.encode(AUTO_SEARCH_NODE_OPTION_KEY));
        if(bytesConfigurationValue == null){
            return SAUTO_SEARCH_NODE_OPTION_DEFAULT_VALUE;
        }
        return Boolean.valueOf(ByteUtil.decodeToUtf8String(bytesConfigurationValue));
    }

    @Override
    public void setAutoSearchNode(boolean autoSearchNode) {
        configurationDatabase.addOrUpdateConfiguration(ByteUtil.encode(AUTO_SEARCH_NODE_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.valueOf(autoSearchNode))));
    }
}