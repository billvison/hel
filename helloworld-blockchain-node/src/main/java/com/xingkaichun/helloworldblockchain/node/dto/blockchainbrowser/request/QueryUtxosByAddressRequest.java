package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import lombok.Data;

@Data
public class QueryUtxosByAddressRequest {

    private String address;
}
