package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

@Data
public class QueryTxosByAddressRequest {

    private String address;
}
