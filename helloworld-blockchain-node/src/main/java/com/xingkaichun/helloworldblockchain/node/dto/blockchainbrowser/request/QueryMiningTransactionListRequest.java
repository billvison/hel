package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.dto.common.page.PageCondition;
import lombok.Data;

@Data
public class QueryMiningTransactionListRequest {

    private PageCondition pageCondition;
}
