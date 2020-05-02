package com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class ReceiveTransactionRequest {

    private TransactionDTO transactionDTO;
}
