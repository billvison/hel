package com.xingkaichun.helloworldblockchain.application.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.NodeDto;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class DeleteNodeRequest {

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
