package com.xingkaichun.helloworldblockchain.node.dto.transaction;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryMiningTransactionListResponse {

    private List<TransactionDTO> transactionDtoList;




    //region get set

    public List<TransactionDTO> getTransactionDtoList() {
        return transactionDtoList;
    }

    public void setTransactionDtoList(List<TransactionDTO> transactionDtoList) {
        this.transactionDtoList = transactionDtoList;
    }

    //endregion
}
