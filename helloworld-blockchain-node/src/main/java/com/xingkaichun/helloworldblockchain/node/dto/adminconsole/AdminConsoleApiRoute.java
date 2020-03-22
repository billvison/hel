package com.xingkaichun.helloworldblockchain.node.dto.adminconsole;

public class AdminConsoleApiRoute {

    public static final String IS_MINER_ACTIVE = "/Api/AdminConsole/IsMinerActive";
    public static final String ACTIVE_MINER = "/Api/AdminConsole/ActiveMiner";
    public static final String DEACTIVE_MINER = "/Api/AdminConsole/DeactiveMiner";

    public static final String IS_SYNCHRONIZER_ACTIVE = "/Api/AdminConsole/IsSynchronizerActive";
    public static final String ACTIVE_SYNCHRONIZER = "/Api/AdminConsole/ActiveSynchronizer";
    public static final String DEACTIVE_SYNCHRONIZER = "/Api/AdminConsole/DeactiveSynchronizer";

    public static final String ADD_NODE = "/Api/AdminConsole/AddNode";
    public static final String UPDATE_BLOCKCHAINBRANCH = "/Api/AdminConsole/UpdateBranchchainBranch";
    public static final String NEW_USER = "/Api/AdminConsole/NewUser";
    public static final String QUERY_MINER_ADDRESS = "/Api/AdminConsole/QueryMinerAddress";
    public static final String RESET_MINER_ADDRESS = "/Api/AdminConsole/ResetMinerAddress";
}