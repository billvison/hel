package com.xingkaichun.helloworldblockchain.application.vo.transaction;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionByTransactionHashResponse {

    private MiningTransactionVo transactionDTO;

    //region get set

    public MiningTransactionVo getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(MiningTransactionVo transactionDTO) {
        this.transactionDTO = transactionDTO;
    }


    //endregion

}
