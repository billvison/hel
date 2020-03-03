package com.xingkaichun.helloworldblockchain.node.dto.blockchain.response;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import lombok.Data;

@Data
public class QueryBlockByBlockHeightResponse {

    private Block block;
}
