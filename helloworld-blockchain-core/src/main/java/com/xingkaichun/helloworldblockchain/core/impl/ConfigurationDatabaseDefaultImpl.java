package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.ConfigurationDatabase;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDBUtil;

public class ConfigurationDatabaseDefaultImpl extends ConfigurationDatabase {

    private static final String CONFIGURATION_TRANSACTION_DATABASE_NAME = "ConfigurationDatabase";
    private String configurationDatabasePath = null;

    //矿工'矿工是否是激活状态'状态存入到数据库时的主键
    private static final String MINE_OPTION_KEY = "IS_MINER_ACTIVE";
    //矿工'矿工是否是激活状态'状态的默认状态
    private static final boolean MINE_OPTION_DEFAULT_VALUE = false;

    public ConfigurationDatabaseDefaultImpl(String rootPath) {
        this.configurationDatabasePath = FileUtil.newPath(rootPath, CONFIGURATION_TRANSACTION_DATABASE_NAME);
    }

    @Override
    public byte[] getConfigurationValue(byte[] configurationKey) {
        byte[] bytesConfigurationValue = KvDBUtil.get(configurationDatabasePath, configurationKey);
        return bytesConfigurationValue;
    }

    @Override
    public void addOrUpdateConfiguration(byte[] configurationKey, byte[] configurationValue) {
        KvDBUtil.put(configurationDatabasePath, configurationKey, configurationValue);
    }

    @Override
    public boolean isMinerActive() {
        byte[] mineOption = getConfigurationValue(ByteUtil.encode(MINE_OPTION_KEY));
        if(mineOption == null){
            return MINE_OPTION_DEFAULT_VALUE;
        }
        return Boolean.valueOf(ByteUtil.decodeToUtf8String(mineOption));
    }

    @Override
    public void activeMiner() {
        addOrUpdateConfiguration(ByteUtil.encode(MINE_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.TRUE)));
    }

    @Override
    public void deactiveMiner() {
        addOrUpdateConfiguration(ByteUtil.encode(MINE_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.FALSE)));
    }
}
