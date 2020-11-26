package com.xingkaichun.helloworldblockchain.node.dto;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class AdminConsoleApiRoute {

    public static final String IS_MINER_ACTIVE = "/Api/AdminConsole/IsMinerActive";
    public static final String ACTIVE_MINER = "/Api/AdminConsole/ActiveMiner";
    public static final String DEACTIVE_MINER = "/Api/AdminConsole/DeactiveMiner";

    public static final String IS_SYNCHRONIZER_ACTIVE = "/Api/AdminConsole/IsSynchronizerActive";
    public static final String ACTIVE_SYNCHRONIZER = "/Api/AdminConsole/ActiveSynchronizer";
    public static final String DEACTIVE_SYNCHRONIZER = "/Api/AdminConsole/DeactiveSynchronizer";

    public static final String ADD_NODE = "/Api/AdminConsole/AddNode";
    public static final String UPDATE_NODE = "/Api/AdminConsole/UpdateNode";
    public static final String DELETE_NODE = "/Api/AdminConsole/DeleteNode";
    public static final String QUERY_ALL_NODE_LIST = "/Api/AdminConsole/QueryAllNodeList";

    public static final String IS_AUTO_SEARCH_NODE = "/Api/AdminConsole/IsAutoSearchNode";
    public static final String SET_AUTO_SEARCH_NODE = "/Api/AdminConsole/SetAutoSearchNode";


    public static final String DELETE_BLOCK = "/Api/AdminConsole/DeleteBlock";


    public static final String ADD_ACCOUNT = "/Api/AdminConsole/AddAccount";
    public static final String DELETE_ACCOUNT = "/Api/AdminConsole/DeleteAccount";
    public static final String QUERY_ALL_ACCOUNT_LIST = "/Api/AdminConsole/QueryAllAccountList";

    public static final String BUILD_TRANSACTION = "/Api/AdminConsole/BuildTransactionDto";

}
