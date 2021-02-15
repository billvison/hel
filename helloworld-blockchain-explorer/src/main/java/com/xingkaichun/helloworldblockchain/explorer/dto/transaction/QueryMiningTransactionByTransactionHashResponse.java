package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionByTransactionHashResponse {

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
