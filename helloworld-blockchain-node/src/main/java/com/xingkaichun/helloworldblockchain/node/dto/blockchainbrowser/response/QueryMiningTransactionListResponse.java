package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import lombok.Data;

import java.util.List;

@Data
public class QueryMiningTransactionListResponse {

    private List<TransactionDTO> transactionDtoList;
}
