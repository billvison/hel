package com.xingkaichun.helloworldblockchain.node.dto.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QuerySpendTransactionOutputListByAddressResponse {

    private List<TransactionOutputDetailView> transactionOutputDetailViewList;

    public List<TransactionOutputDetailView> getTransactionOutputDetailViewList() {
        return transactionOutputDetailViewList;
    }

    public void setTransactionOutputDetailViewList(List<TransactionOutputDetailView> transactionOutputDetailViewList) {
        this.transactionOutputDetailViewList = transactionOutputDetailViewList;
    }
}
