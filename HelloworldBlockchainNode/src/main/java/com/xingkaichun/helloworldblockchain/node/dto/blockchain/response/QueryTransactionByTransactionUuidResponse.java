package com.xingkaichun.helloworldblockchain.node.dto.blockchain.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import lombok.Data;

@Data
public class QueryTransactionByTransactionUuidResponse {

    private Transaction transaction;
}
