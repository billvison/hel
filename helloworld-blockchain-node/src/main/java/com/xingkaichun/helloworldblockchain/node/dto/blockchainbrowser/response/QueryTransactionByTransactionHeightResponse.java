package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryTransactionByTransactionHeightResponse {

    private List<Transaction> transactionList;
}
