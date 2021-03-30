package com.xingkaichun.helloworldblockchain.explorer.vo.node;

import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.BaseNodeDto;

/**
 *
 * @author 邢开春 409060350@qq.com
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
