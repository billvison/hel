package com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.Node;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class UpdateNodeRequest {

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