package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.BlockchainBranchBlockDto;
import lombok.Data;

import java.util.List;

@Data
public class QueryBlockchainBranchResponse {

    private List<BlockchainBranchBlockDto> blockList;
}
