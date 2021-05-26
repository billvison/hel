package com.xingkaichun.helloworldblockchain.application.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryUnconfirmedTransactionByTransactionHashResponse {

    private UnconfirmedTransactionVo transactionDTO;

    //region get set

    public UnconfirmedTransactionVo getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(UnconfirmedTransactionVo transactionDTO) {
        this.transactionDTO = transactionDTO;
    }


    //endregion

}
