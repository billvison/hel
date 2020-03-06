package com.xingkaichun.helloworldblockchain.node.dto.blockchain.request;

import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.model.key.StringPrivateKey;
import lombok.Data;

@Data
public class SignatureTransactDtoRequest {

    private TransactionDTO transactionDTO;
    private StringPrivateKey stringPrivateKey;
}
