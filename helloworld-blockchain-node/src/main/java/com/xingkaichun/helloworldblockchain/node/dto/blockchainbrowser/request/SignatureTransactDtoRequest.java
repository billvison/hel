package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class SignatureTransactDtoRequest {

    private TransactionDTO transactionDTO;
    private StringPrivateKey stringPrivateKey;
}
