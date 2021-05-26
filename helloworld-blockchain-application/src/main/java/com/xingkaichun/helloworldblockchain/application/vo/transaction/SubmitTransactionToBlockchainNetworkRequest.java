package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDto;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class SubmitTransactionToBlockchainNetworkRequest {

    private TransactionDto transactionDTO;




    //region get set

    public TransactionDto getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(TransactionDto transactionDTO) {
        this.transactionDTO = transactionDTO;
    }


    //endregion
}
