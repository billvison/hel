package com.xingkaichun.helloworldblockchain.node.transport.dto.nodeserver.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import lombok.Data;

@Data
public class ReceiveTransactionRequest {

    private TransactionDTO transactionDTO;
}
