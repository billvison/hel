package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class SubmitTransactionToBlockchainNetworkRequest {

    private TransactionDto transaction;




    //region get set

    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }


    //endregion
}
