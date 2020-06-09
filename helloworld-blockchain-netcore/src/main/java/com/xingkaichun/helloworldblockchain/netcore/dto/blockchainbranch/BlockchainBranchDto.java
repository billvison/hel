package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbranch;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainBranchDto {

    private List<BlockchainBranchBlockDto> blockList;




    //region get set

    public List<BlockchainBranchBlockDto> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<BlockchainBranchBlockDto> blockList) {
        this.blockList = blockList;
    }

    //endregion
}
