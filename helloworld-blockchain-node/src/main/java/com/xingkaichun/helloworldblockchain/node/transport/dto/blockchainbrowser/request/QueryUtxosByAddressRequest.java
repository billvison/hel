package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.common.page.PageCondition;
import lombok.Data;

@Data
public class QueryUtxosByAddressRequest {

    private String address;

    private PageCondition pageCondition;
}
