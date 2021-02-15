package com.xingkaichun.helloworldblockchain.explorer.dto.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDto;

/**
 *
 * @author 邢开春
 */
public class DeleteNodeRequest {

    private BaseNodeDto node;




    //region get set
    public BaseNodeDto getNode() {
        return node;
    }

    public void setNode(BaseNodeDto node) {
        this.node = node;
    }
    //endregion
}
