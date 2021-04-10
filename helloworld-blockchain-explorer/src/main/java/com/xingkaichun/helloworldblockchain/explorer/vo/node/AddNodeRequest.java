package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class AddNodeRequest {

    private NodeEntity node;




    //region get set
    public NodeEntity getNode() {
        return node;
    }

    public void setNode(NodeEntity node) {
        this.node = node;
    }
    //endregion
}
