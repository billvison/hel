package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.model.Block;
import lombok.Data;

@Data
public class QueryBlockDtoByBlockHashResponse {

    private Block block ;
}
