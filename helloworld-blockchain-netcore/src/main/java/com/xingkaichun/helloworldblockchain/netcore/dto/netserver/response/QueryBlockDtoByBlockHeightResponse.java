package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.response;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
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
