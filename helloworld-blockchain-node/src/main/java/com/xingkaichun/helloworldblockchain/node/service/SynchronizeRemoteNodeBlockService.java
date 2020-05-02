package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;

/**
 * 同步节点数据service
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface SynchronizeRemoteNodeBlockService {

    /**
     * 同步远程节点的区块到本地区块链系统
     */
    void synchronizeRemoteNodeBlock(Node node) throws Exception;
}
