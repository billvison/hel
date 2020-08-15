package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.SimpleNodeDto;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class DeleteNodeRequest {

    private SimpleNodeDto node;




    //region get set
    public SimpleNodeDto getNode() {
        return node;
    }

    public void setNode(SimpleNodeDto node) {
        this.node = node;
    }
    //endregion
}
