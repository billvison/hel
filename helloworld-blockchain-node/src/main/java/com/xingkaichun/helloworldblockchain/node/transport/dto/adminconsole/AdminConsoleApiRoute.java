package com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole;

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
    public static final String QUERY_NODE_LIST = "/Api/AdminConsole/QueryNodeList";

    public static final String UPDATE_BLOCKCHAINBRANCH = "/Api/AdminConsole/UpdateBranchchainBranch";
    public static final String UPDATE_ADMIN_USER = "/Api/AdminConsole/UpdateAdminUserRequest";
    public static final String QUERY_MINER_ADDRESS = "/Api/AdminConsole/QueryMinerAddress";
    public static final String SET_MINER_ADDRESS = "/Api/AdminConsole/SetMinerAddress";
    public static final String IS_AUTO_SEARCH_NODE = "/Api/AdminConsole/IsAutoSearchNode";
    public static final String SET_AUTO_SEARCH_NODE = "/Api/AdminConsole/SetAutoSearchNode";


    public static final String REMOVE_BLOCK = "/Api/AdminConsole/RemoveBlock";

    public static final String GET_NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD = "/Api/AdminConsole/GetNodeErrorConnectionTimesRemoveThreshold";
    public static final String SET_NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD = "/Api/AdminConsole/SetNodeErrorConnectionTimesRemoveThreshold";

    public static final String GET_NODE_SEARCH_NEW_NODE_TIME_INTERVAL = "/Api/AdminConsole/GetNodeSearchNewNodeTimeInterval";
    public static final String SET_NODE_SEARCH_NEW_NODE_TIME_INTERVAL = "/Api/AdminConsole/SetNodeSearchNewNodeTimeInterval";

    public static final String GET_SEARCH_NEW_BLOCKS_TIME_INTERVAL = "/Api/AdminConsole/GetSearchNewBlocksTimeInterval";
    public static final String SET_SEARCH_NEW_BLOCKS_TIME_INTERVAL = "/Api/AdminConsole/SetSearchNewBlocksTimeInterval";

    public static final String GET_CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL = "/Api/AdminConsole/GetCheckLocalBlockChainHeightIsHighTimeInterval";
    public static final String SET_CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL = "/Api/AdminConsole/SetCheckLocalBlockChainHeightIsHighTimeInterval";



}
