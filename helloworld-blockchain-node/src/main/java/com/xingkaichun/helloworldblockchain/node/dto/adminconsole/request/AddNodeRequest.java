package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class AddNodeRequest {

    private NodeDto node;




    //region get set
    public NodeDto getNode() {
        return node;
    }

    public void setNode(NodeDto node) {
        this.node = node;
    }
    //endregion
}
