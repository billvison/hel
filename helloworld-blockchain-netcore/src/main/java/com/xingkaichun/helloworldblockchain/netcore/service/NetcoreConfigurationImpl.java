package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDBUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NetcoreConfigurationImpl implements NetcoreConfiguration {

    private String netcorePath;

    private static final String NETCORE_CONFIGURATION_DATABASE_NAME = "NetcoreConfigurationDatabase";
    private String netcoreConfigurationDatabasePath;


    //同步器'同步器是否是激活状态'状态存入到数据库时的主键
    private static final String SYNCHRONIZER_OPTION_KEY = "IS_SYNCHRONIZER_ACTIVE";
    //同步器'同步器是否是激活状态'开关的默认状态
    private static final boolean SYNCHRONIZER_OPTION_DEFAULT_VALUE = false;

    //节点搜索器'是否自动搜索节点'状态存入到数据库时的主键
    private static final String AUTO_SEARCH_NODE_OPTION_KEY = "IS_AUTO_SEARCH_NODE";
    //节点搜索器'是否自动搜索节点'开关的默认状态
    private static final boolean SAUTO_SEARCH_NODE_OPTION_DEFAULT_VALUE = false;

    //在区块链网络中自动搜寻新的节点的间隔时间
    private static final long SEARCH_NODE_TIME_INTERVAL = 1000 * 60 * 2;
    //在区块链网络中自动搜索节点的区块链高度
    private static final long SEARCH_BLOCKCHAIN_HEIGHT_TIME_INTERVAL = 1000 * 60 * 2;
    //在区块链网络中自动搜寻新的区块的间隔时间。
    private static final long SEARCH_BLOCKS_TIME_INTERVAL = 1000 * 60 * 2;
    //区块高度广播时间间隔
    private static final long BLOCKCHAIN_HEIGHT_BROADCASTER_TIME_INTERVAL = 1000 * 20;
    //区块广播时间间隔。
    private static final long BLOCK_BROADCASTER_TIME_INTERVAL = 1000 * 20;
    //定时将种子节点加入本地区块链网络的时间间隔。
    private static final long ADD_SEED_NODE_TIME_INTERVAL = 1000 * 60 * 2;
    //广播自己节点的时间间隔。
    private static final long NODE_BROADCAST_TIME_INTERVAL = 1000 * 60 * 2;


    public NetcoreConfigurationImpl(String netcorePath) {
        if(StringUtil.isNullOrEmpty(netcorePath)){
            throw new NullPointerException("netcore Path不能为空。");
        }
        FileUtil.mkdirs(netcorePath);

        this.netcorePath = netcorePath;
        this.netcoreConfigurationDatabasePath = FileUtil.newPath(netcorePath, NETCORE_CONFIGURATION_DATABASE_NAME);
    }


    @Override
    public String getNetcorePath() {
        return netcorePath;
    }

    @Override
    public boolean isSynchronizerActive() {
        byte[] bytesConfigurationValue = getConfigurationValue(ByteUtil.encode(SYNCHRONIZER_OPTION_KEY));
        if(bytesConfigurationValue == null){
            return SYNCHRONIZER_OPTION_DEFAULT_VALUE;
        }
        return Boolean.valueOf(ByteUtil.decodeToUtf8String(bytesConfigurationValue));
    }

    @Override
    public void activeSynchronizer() {
        addOrUpdateConfiguration(ByteUtil.encode(SYNCHRONIZER_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.TRUE)));
    }

    @Override
    public void deactiveSynchronizer() {
        addOrUpdateConfiguration(ByteUtil.encode(SYNCHRONIZER_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.FALSE)));
    }

    @Override
    public boolean isAutoSearchNode() {
        byte[] bytesConfigurationValue = getConfigurationValue(ByteUtil.encode(AUTO_SEARCH_NODE_OPTION_KEY));
        if(bytesConfigurationValue == null){
            return SAUTO_SEARCH_NODE_OPTION_DEFAULT_VALUE;
        }
        return Boolean.valueOf(ByteUtil.decodeToUtf8String(bytesConfigurationValue));
    }

    @Override
    public void setAutoSearchNode(boolean autoSearchNode) {
        addOrUpdateConfiguration(ByteUtil.encode(AUTO_SEARCH_NODE_OPTION_KEY),ByteUtil.encode(String.valueOf(Boolean.valueOf(autoSearchNode))));
    }

    @Override
    public long getSearchNodeTimeInterval() {
        return SEARCH_NODE_TIME_INTERVAL;
    }

    @Override
    public long getSearchBlockchainHeightTimeInterval() {
        return SEARCH_BLOCKCHAIN_HEIGHT_TIME_INTERVAL;
    }

    @Override
    public long getSearchBlockTimeInterval() {
        return SEARCH_BLOCKS_TIME_INTERVAL;
    }

    @Override
    public long getBlockchainHeightBroadcastTimeInterval() {
        return BLOCKCHAIN_HEIGHT_BROADCASTER_TIME_INTERVAL;
    }

    @Override
    public long getBlockBroadcastTimeInterval() {
        return BLOCK_BROADCASTER_TIME_INTERVAL;
    }

    @Override
    public long getAddSeedNodeTimeInterval() {
        return ADD_SEED_NODE_TIME_INTERVAL;
    }

    @Override
    public long getNodeBroadcastTimeInterval() {
        return NODE_BROADCAST_TIME_INTERVAL;
    }


    private byte[] getConfigurationValue(byte[] configurationKey) {
        byte[] bytesConfigurationValue = KvDBUtil.get(netcoreConfigurationDatabasePath, configurationKey);
        return bytesConfigurationValue;
    }

    private void addOrUpdateConfiguration(byte[] configurationKey, byte[] configurationValue) {
        KvDBUtil.put(netcoreConfigurationDatabasePath, configurationKey, configurationValue);
    }
}