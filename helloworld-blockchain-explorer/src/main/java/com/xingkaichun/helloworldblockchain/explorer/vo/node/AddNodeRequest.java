package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDTO;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class AddNodeRequest {

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
