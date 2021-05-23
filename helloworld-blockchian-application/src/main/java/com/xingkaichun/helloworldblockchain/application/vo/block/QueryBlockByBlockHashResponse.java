package com.xingkaichun.helloworldblockchain.application.vo.block;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryBlockByBlockHashResponse {

    private BlockView blockView ;


    //region get set
    public BlockView getBlockView() {
        return blockView;
    }

    public void setBlockView(BlockView blockView) {
        this.blockView = blockView;
    }
    //endregion
}
