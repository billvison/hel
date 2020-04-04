package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.dto.common.page.PageCondition;
import lombok.Data;

@Data
public class QueryUtxosByAddressRequest {

    private String address;

    private PageCondition pageCondition;
}
