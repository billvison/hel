package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.NodeDTO;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class DeleteNodeRequest {

    private NodeDTO node;




    //region get set
    public NodeDTO getNode() {
        return node;
    }

    public void setNode(NodeDTO node) {
        this.node = node;
    }
    //endregion
}
