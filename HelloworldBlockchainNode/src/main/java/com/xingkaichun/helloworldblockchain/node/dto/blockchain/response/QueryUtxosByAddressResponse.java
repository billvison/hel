package com.xingkaichun.helloworldblockchain.node.dto.blockchain.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import lombok.Data;

import java.util.List;

@Data
public class QueryUtxosByAddressResponse {

    private List<TransactionOutput> utxos;
}
