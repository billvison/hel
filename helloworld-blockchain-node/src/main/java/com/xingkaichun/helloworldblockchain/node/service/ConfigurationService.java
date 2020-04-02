package com.xingkaichun.helloworldblockchain.node.service;



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
     * 是否允许节点自动在区块链网络中搜索节点
     */
    boolean autoSearchNode();
    /**
     * 设置是否允许节点自动在区块链网络中搜索节点
     */
    void setAutoSearchNode(boolean autoSearchNewNode);

    /**
     * 本地节点发现某一个节点错误次数过多，则删除该节点
     * @return
     */
    long getNodeErrorConnectionTimesRemoveThreshold();
    /**
     * 本地节点发现某一个节点错误次数过多，则删除该节点
     * 设置这个错误次数的阈值
     */
    void setNodeErrorConnectionTimesRemoveThreshold(long timestamp);

    /**
     * 在区块链网络中自动搜寻新的节点的间隔时间
     * @return
     */
    long getNodeSearchNewNodeTimeInterval();
    /**
     * 在区块链网络中自动搜寻新的节点的间隔时间
     * 设置这个间隔时间
     */
    void setNodeSearchNewNodeTimeInterval(long timestamp);

    /**
     * 在区块链网络中自动搜寻新的区块的间隔时间
     */
    long getSearchNewBlocksTimeInterval();
    /**
     * 在区块链网络中自动搜寻新的区块的间隔时间
     * 设置这个间隔时间
     */
    void setSearchNewBlocksTimeInterval(long timestamp);

    /**
     * 检查自己的区块链高度在区块链网络中是否是最大的高度的时间间隔
     */
    long getCheckLocalBlockChainHeightIsHighTimeInterval();
    /**
     * 检查自己的区块链高度在区块链网络中是否是最大的高度的时间间隔
     * 设置这个间隔时间
     */
    void setCheckLocalBlockChainHeightIsHighTimeInterval(long timestamp);
}
