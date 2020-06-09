package com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.request;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
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
