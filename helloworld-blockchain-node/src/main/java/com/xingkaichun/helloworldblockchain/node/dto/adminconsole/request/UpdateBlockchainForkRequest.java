package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.fork.BlockchainForkBlockDto;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class UpdateBlockchainForkRequest {

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
