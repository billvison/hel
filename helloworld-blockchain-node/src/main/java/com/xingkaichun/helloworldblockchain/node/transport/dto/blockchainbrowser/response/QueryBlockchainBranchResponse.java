package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbranch.BlockchainBranchBlockDto;
import lombok.Data;

import java.util.List;

@Data
public class QueryBlockchainBranchResponse {

    private List<BlockchainBranchBlockDto> blockList;
}
