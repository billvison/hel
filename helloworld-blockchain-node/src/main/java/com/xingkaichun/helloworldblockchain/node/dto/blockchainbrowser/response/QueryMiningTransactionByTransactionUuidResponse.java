package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import lombok.Data;

@Data
public class QueryMiningTransactionByTransactionUuidResponse {

    private TransactionDTO transactionDTO;
}
