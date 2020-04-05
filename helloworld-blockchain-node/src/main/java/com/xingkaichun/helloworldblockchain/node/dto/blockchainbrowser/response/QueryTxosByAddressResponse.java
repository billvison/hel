package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import lombok.Data;

import java.util.List;

@Data
public class QueryTxosByAddressResponse {

    private List<TransactionOutput> txos;
}
