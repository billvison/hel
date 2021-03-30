package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDTO;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class DeleteNodeRequest {

    private BaseNodeDTO node;




    //region get set
    public BaseNodeDTO getNode() {
        return node;
    }

    public void setNode(BaseNodeDTO node) {
        this.node = node;
    }
    //endregion
}
