package com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import lombok.Data;

@Data
public class QueryBlockDtoByBlockHeightResponse {

    private BlockDTO blockDTO ;
}
