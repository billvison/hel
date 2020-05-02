package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.dto.common.page.PageCondition;
import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryTransactionByTransactionHeightRequest {

    private PageCondition pageCondition;
}
