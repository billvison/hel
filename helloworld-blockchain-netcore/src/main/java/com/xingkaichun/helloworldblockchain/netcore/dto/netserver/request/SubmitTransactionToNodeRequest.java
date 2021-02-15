package com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

/**
 *
 * @author 邢开春
 */
public class SubmitTransactionToNodeRequest {

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
