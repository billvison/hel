package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.node.Node;

public interface RemoteNodeService {

    /**
     * 同步节点的区块到本地区块链
     */
    void synchronizeRemoteNodeBlock(Node node) throws Exception;
}
