package com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch;

import lombok.Data;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class BlockchainBranchDto {

    private List<BlockchainBranchBlockDto> blockList;
}
