package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.model.transaction.TransactionOutput;
import lombok.Data;

import java.util.List;

@Data
public class QueryUtxosByAddressResponse {

    private List<TransactionOutput> utxos;
}
