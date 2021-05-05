package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class PostBlockRequest {

    private BlockDTO block;

    public BlockDTO getBlock() {
        return block;
    }

    public void setBlock(BlockDTO block) {
        this.block = block;
    }
}
