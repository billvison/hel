package com.xingkaichun.helloworldblockchain.node.dto.block;

import com.xingkaichun.helloworldblockchain.core.model.Block;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryBlockDtoByBlockHeightResponse {

    private Block block ;




    //region get set

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    //endregion
}
