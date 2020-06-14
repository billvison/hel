package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.Node;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class AddNodeRequest {

    private Node node;




    //region get set
    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
    //endregion
}
