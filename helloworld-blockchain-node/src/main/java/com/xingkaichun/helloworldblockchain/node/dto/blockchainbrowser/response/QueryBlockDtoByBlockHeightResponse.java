package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.model.Block;
import lombok.Data;

@Data
public class QueryBlockDtoByBlockHeightResponse {

    private Block block ;
}
