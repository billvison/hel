package com.xingkaichun.helloworldblockchain.explorer.dto.block;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryBlockDtoByBlockHeightResponse {

    private QueryBlockDtoByBlockHashResponse.BlockDto blockDto ;




    //region get set

    public QueryBlockDtoByBlockHashResponse.BlockDto getBlockDto() {
        return blockDto;
    }

    public void setBlockDto(QueryBlockDtoByBlockHashResponse.BlockDto blockDto) {
        this.blockDto = blockDto;
    }


    //endregion
}
