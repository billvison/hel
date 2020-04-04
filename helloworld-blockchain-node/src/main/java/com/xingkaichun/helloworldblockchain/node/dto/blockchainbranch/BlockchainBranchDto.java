package com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch;

import lombok.Data;

import java.util.List;

@Data
public class BlockchainBranchDto {

    private List<BlockchainBranchBlockDto> blockList;
}
