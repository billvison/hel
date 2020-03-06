package com.xingkaichun.helloworldblockchain.node.dto.blockchain.response;

import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import lombok.Data;

@Data
public class SubmitNormalTransactionResponse {

    TransactionDTO transactionDTO;
}
