package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbranch;

import lombok.Data;

import java.util.List;

@Data
public class BlockchainBranchDto {

    private List<BlockchainBranchBlockDto> blockList;
}
