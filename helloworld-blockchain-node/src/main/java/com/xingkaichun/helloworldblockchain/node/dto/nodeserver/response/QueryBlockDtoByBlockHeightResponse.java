package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryBlockDtoByBlockHeightResponse {

    private BlockDTO blockDTO ;
}
