package com.xingkaichun.helloworldblockchain.node.dto.node.request;

import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import lombok.Data;

@Data
public class ReceiveTransactionRequest {

    private TransactionDTO transactionDTO;
}
