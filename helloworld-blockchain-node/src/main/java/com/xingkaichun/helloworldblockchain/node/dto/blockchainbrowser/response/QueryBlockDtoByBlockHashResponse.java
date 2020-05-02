package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryBlockDtoByBlockHashResponse {

    private Block block ;
}
