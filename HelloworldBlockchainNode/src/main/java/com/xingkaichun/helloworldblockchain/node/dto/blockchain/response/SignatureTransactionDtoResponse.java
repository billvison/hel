package com.xingkaichun.helloworldblockchain.node.dto.blockchain.response;

import com.xingkaichun.helloworldblockchain.core.dto.TransactionDTO;
import lombok.Data;

@Data
public class SignatureTransactionDtoResponse {

    private TransactionDTO transactionDTO;
}
