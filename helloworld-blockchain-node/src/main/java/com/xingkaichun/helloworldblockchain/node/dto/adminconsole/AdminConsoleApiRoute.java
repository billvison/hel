package com.xingkaichun.helloworldblockchain.node.dto.adminconsole;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
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

    public static final String UPDATE_BLOCKCHAINBFORK = "/Api/AdminConsole/UpdateBlockchainFork";
    public static final String UPDATE_ADMIN_USER = "/Api/AdminConsole/UpdateAdminUserRequest";
    public static final String QUERY_MINER_ADDRESS = "/Api/AdminConsole/QueryMinerAddress";
    public static final String SET_MINER_ADDRESS = "/Api/AdminConsole/SetMinerAddress";
    public static final String IS_AUTO_SEARCH_NODE = "/Api/AdminConsole/IsAutoSearchNode";
    public static final String SET_AUTO_SEARCH_NODE = "/Api/AdminConsole/SetAutoSearchNode";


    public static final String REMOVE_BLOCK = "/Api/AdminConsole/RemoveBlock";


    public static final String GET_CONFIGURATION_BY_CONFIGURATION_KEY_KEY = "/Api/AdminConsole/GetConfigurationByConfigurationKey";
    public static final String SET_CONFIGURATION = "/Api/AdminConsole/SetConfiguration";


}
