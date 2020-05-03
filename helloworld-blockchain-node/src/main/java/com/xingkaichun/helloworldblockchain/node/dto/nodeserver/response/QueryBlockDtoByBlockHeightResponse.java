package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryBlockDtoByBlockHeightResponse {

    private BlockDTO blockDTO ;




    //region get set

    public BlockDTO getBlockDTO() {
        return blockDTO;
    }

    public void setBlockDTO(BlockDTO blockDTO) {
        this.blockDTO = blockDTO;
    }

    //endregion
}
