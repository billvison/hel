package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.model.key.StringPrivateKey;
import lombok.Data;

@Data
public class SignatureTransactDtoRequest {

    private TransactionDTO transactionDTO;
    private StringPrivateKey stringPrivateKey;
}
