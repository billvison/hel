package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import java.math.BigInteger;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class RemoveBlockRequest {

    /**
     * 删除区块的高度。因为区块是连续的，所以大于等于这个高度的区块都将被删除
     */
    private BigInteger blockHeight;




    //region get set
    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(BigInteger blockHeight) {
        this.blockHeight = blockHeight;
    }
    //endregion
}
