package com.xingkaichun.helloworldblockchain.explorer.dto.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class UpdateNodeRequest {

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
