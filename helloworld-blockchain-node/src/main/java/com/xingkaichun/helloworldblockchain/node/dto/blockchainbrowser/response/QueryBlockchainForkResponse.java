package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.fork.BlockchainForkBlockDto;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryBlockchainForkResponse {

    private List<BlockchainForkBlockDto> blockList;




    //region get set

    public List<BlockchainForkBlockDto> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<BlockchainForkBlockDto> blockList) {
        this.blockList = blockList;
    }

    //endregion
}
