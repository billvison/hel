package com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ReceiveTransactionRequest {

    private TransactionDTO transactionDTO;




    //region get set

    public TransactionDTO getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(TransactionDTO transactionDTO) {
        this.transactionDTO = transactionDTO;
    }

    //endregion
}
