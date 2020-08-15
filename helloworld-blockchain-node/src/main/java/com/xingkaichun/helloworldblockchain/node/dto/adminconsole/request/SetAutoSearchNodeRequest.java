package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SetAutoSearchNodeRequest {

    private boolean autoSearchNode;




    //region get set
    public boolean isAutoSearchNode() {
        return autoSearchNode;
    }

    public void setAutoSearchNode(boolean autoSearchNode) {
        this.autoSearchNode = autoSearchNode;
    }
    //endregion
}
