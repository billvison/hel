package com.xingkaichun.helloworldblockchain.explorer.dto.block;

/**
 *
 * @author 邢开春
 */
public class QueryBlockDtoByBlockHashRequest {

    private String blockHash;




    //region get set

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    //endregion
}
