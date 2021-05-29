package com.xingkaichun.helloworldblockchain.application.vo.block;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryBlockByBlockHashResponse {

    private BlockVo block;


    //region get set
    public BlockVo getBlock() {
        return block;
    }

    public void setBlock(BlockVo block) {
        this.block = block;
    }
    //endregion
}
