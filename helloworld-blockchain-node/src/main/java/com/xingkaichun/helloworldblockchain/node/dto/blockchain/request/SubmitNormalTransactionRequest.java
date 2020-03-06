package com.xingkaichun.helloworldblockchain.node.dto.blockchain.request;

import com.xingkaichun.helloworldblockchain.node.dto.blockchain.NormalTransactionDto;
import lombok.Data;

@Data
public class SubmitNormalTransactionRequest {

    private NormalTransactionDto normalTransactionDto;
}
