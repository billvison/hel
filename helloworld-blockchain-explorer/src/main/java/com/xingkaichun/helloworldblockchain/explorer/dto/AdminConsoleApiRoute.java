package com.xingkaichun.helloworldblockchain.explorer.dto;

/**
 * 管理区块链路由
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class AdminConsoleApiRoute {
    //矿工是否激活
    public static final String IS_MINER_ACTIVE = "/Api/AdminConsole/IsMinerActive";
    //激活矿工
    public static final String ACTIVE_MINER = "/Api/AdminConsole/ActiveMiner";
    //停用矿工
    public static final String DEACTIVE_MINER = "/Api/AdminConsole/DeactiveMiner";



    //同步器是否激活
    public static final String IS_SYNCHRONIZER_ACTIVE = "/Api/AdminConsole/IsSynchronizerActive";
    //激活同步器
    public static final String ACTIVE_SYNCHRONIZER = "/Api/AdminConsole/ActiveSynchronizer";
    //停用同步器
    public static final String DEACTIVE_SYNCHRONIZER = "/Api/AdminConsole/DeactiveSynchronizer";



    //新增节点
    public static final String ADD_NODE = "/Api/AdminConsole/AddNode";
    //更新节点信息
    public static final String UPDATE_NODE = "/Api/AdminConsole/UpdateNode";
    //删除节点
    public static final String DELETE_NODE = "/Api/AdminConsole/DeleteNode";
    //查询所有节点
    public static final String QUERY_ALL_NODE_LIST = "/Api/AdminConsole/QueryAllNodeList";



    //是否开启自动寻找区块链节点的功能
    public static final String IS_AUTO_SEARCH_NODE = "/Api/AdminConsole/IsAutoSearchNode";
    //设置是否允许自动寻找区块链节点
    public static final String SET_AUTO_SEARCH_NODE = "/Api/AdminConsole/SetAutoSearchNode";



    //删除区块
    public static final String DELETE_BLOCK = "/Api/AdminConsole/DeleteBlock";



    //新增账户
    public static final String ADD_ACCOUNT = "/Api/AdminConsole/AddAccount";
    //删除账户
    public static final String DELETE_ACCOUNT = "/Api/AdminConsole/DeleteAccount";
    //查询所有的账户
    public static final String QUERY_ALL_ACCOUNT_LIST = "/Api/AdminConsole/QueryAllAccountList";



    //构建交易
    public static final String BUILD_TRANSACTION = "/Api/AdminConsole/BuildTransactionDto";

}
