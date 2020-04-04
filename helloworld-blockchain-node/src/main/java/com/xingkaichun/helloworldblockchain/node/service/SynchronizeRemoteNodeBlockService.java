package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.Node;

public interface SynchronizeRemoteNodeBlockService {

    /**
     * 同步远程节点的区块到本地区块链系统
     */
    void synchronizeRemoteNodeBlock(Node node) throws Exception;
}
