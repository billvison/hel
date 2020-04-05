package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class QueryTransactionByTransactionHeightResponse {

    private List<Transaction> transactionList;
}
