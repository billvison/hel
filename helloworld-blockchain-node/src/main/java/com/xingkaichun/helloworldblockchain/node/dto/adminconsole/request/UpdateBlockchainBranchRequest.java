package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.BlockchainBranchBlockDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateBlockchainBranchRequest {

    private List<BlockchainBranchBlockDto> blockList;
}
