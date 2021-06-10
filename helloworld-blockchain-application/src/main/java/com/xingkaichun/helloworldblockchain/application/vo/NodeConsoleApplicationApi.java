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



    //是否"自动搜索新区块"
    public static final String IS_AUTO_SEARCH_BLOCK = "/Api/NodeConsoleApplication/IsAutoSearchBlock";
    //开启"自动搜索新区块"选项
    public static final String ACTIVE_AUTO_SEARCH_BLOCK = "/Api/NodeConsoleApplicationle/ActiveAutoSearchBlock";
    //关闭"自动搜索新区块"选项
    public static final String DEACTIVE_AUTO_SEARCH_BLOCK = "/Api/NodeConsoleApplication/DeactiveAutoSearchBlock";



    //是否开启了自动寻找区块链节点的功能
    public static final String IS_AUTO_SEARCH_NODE = "/Api/NodeConsoleApplication/IsAutoSearchNode";
    //开启"自动搜索节点"选项
    public static final String ACTIVE_AUTO_SEARCH_NODE = "/Api/NodeConsoleApplication/ActiveAutoSearchNode";
    //关闭"自动搜索节点"选项
    public static final String DEACTIVE_AUTO_SEARCH_NODE = "/Api/NodeConsoleApplication/DeactiveAutoSearchNode";



    //新增节点
    public static final String ADD_NODE = "/Api/NodeConsoleApplication/AddNode";
    //更新节点信息
    public static final String UPDATE_NODE = "/Api/NodeConsoleApplication/UpdateNode";
    //删除节点
    public static final String DELETE_NODE = "/Api/NodeConsoleApplication/DeleteNode";
    //查询所有节点
    public static final String QUERY_ALL_NODES = "/Api/NodeConsoleApplication/QueryAllNodes";



    //删除区块
    public static final String DELETE_BLOCKS = "/Api/NodeConsoleApplication/DeleteBlocks";

}
