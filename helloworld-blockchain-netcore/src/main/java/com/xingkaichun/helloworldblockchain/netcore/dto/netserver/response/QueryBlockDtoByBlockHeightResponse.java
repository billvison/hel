package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryBlockDtoByBlockHeightResponse {

    private BlockDTO block ;




    //region get set

    public BlockDTO getBlock() {
        return block;
    }

    public void setBlock(BlockDTO block) {
        this.block = block;
    }


    //endregion
}
