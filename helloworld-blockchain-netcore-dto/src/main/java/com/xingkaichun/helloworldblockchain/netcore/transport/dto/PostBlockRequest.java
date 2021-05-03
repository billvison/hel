package com.xingkaichun.helloworldblockchain.netcore.transport.dto;

public class PostBlockRequest {

    private BlockDTO block;

    public BlockDTO getBlock() {
        return block;
    }

    public void setBlock(BlockDTO block) {
        this.block = block;
    }
}
