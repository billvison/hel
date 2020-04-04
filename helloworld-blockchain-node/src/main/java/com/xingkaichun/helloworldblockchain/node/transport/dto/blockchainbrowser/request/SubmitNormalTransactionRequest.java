package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser.NormalTransactionDto;
import lombok.Data;

@Data
public class SubmitNormalTransactionRequest {

    private NormalTransactionDto normalTransactionDto;
}
