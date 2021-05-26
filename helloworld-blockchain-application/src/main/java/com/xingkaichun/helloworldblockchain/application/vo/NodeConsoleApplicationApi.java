package com.xingkaichun.helloworldblockchain.application.vo;

/**
 * 节点控制台应用接口
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeConsoleApplicationApi {

    //矿工是否激活
    public static final String IS_MINER_ACTIVE = "/Api/NodeConsoleApplication/IsMinerActive";
    //激活矿工
    public static final String ACTIVE_MINER = "/Api/NodeConsoleApplication/ActiveMiner";
    //停用矿工
    public static final String DEACTIVE_MINER = "/Api/NodeConsoleApplication/DeactiveMiner";



    //同步器是否激活
    public static final String IS_SYNCHRONIZER_ACTIVE = "/Api/NodeConsoleApplication/IsSynchronizerActive";
    //激活同步器
    public static final String ACTIVE_SYNCHRONIZER = "/Api/NodeConsoleApplicationle/ActiveSynchronizer";
    //停用同步器
    public static final String DEACTIVE_SYNCHRONIZER = "/Api/NodeConsoleApplication/DeactiveSynchronizer";



    //新增节点
    public static final String ADD_NODE = "/Api/NodeConsoleApplication/AddNode";
    //更新节点信息
    public static final String UPDATE_NODE = "/Api/NodeConsoleApplication/UpdateNode";
    //删除节点
    public static final String DELETE_NODE = "/Api/NodeConsoleApplication/DeleteNode";
    //查询所有节点
    public static final String QUERY_ALL_NODES = "/Api/NodeConsoleApplication/QueryAllNodes";



    //是否开启了自动寻找区块链节点的功能
    public static final String IS_AUTO_SEARCH_NODE = "/Api/NodeConsoleApplication/IsAutoSearchNode";
    //设置是否允许自动寻找区块链节点
    public static final String SET_AUTO_SEARCH_NODE = "/Api/NodeConsoleApplication/SetAutoSearchNode";



    //删除区块
    public static final String DELETE_BLOCKS = "/Api/NodeConsoleApplication/DeleteBlocks";

}
