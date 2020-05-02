package com.xingkaichun.helloworldblockchain.core.listen;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.enums.BlockChainActionEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class BlockChainActionData {

    private List<Block> blockList;
    private BlockChainActionEnum blockChainActionEnum;

    public BlockChainActionData() {
    }

    public BlockChainActionData(List<Block> blockList, BlockChainActionEnum blockChainActionEnum) {
        this.blockList = blockList;
        this.blockChainActionEnum = blockChainActionEnum;
    }

    public BlockChainActionData(Block block, BlockChainActionEnum blockChainActionEnum) {
        if(block != null){
            blockList = new ArrayList<>();
            blockList.add(block);
        }
        this.blockChainActionEnum = blockChainActionEnum;
    }
}
