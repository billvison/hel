package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.NormalTransactionDto;
import lombok.Data;

@Data
public class SubmitNormalTransactionRequest {

    private NormalTransactionDto normalTransactionDto;
}
