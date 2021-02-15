package com.xingkaichun.helloworldblockchain.netcore.transport.dto;


import java.io.Serializable;
import java.util.List;

/**
 * 交易
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction
 *
 * @author 邢开春
 */
public class TransactionDTO implements Serializable {

    //交易输入
    private List<TransactionInputDTO> transactionInputDtoList;
    //交易输出
    private List<TransactionOutputDTO> transactionOutputDtoList;




    //region get set

    public List<TransactionInputDTO> getTransactionInputDtoList() {
        return transactionInputDtoList;
    }

    public void setTransactionInputDtoList(List<TransactionInputDTO> transactionInputDtoList) {
        this.transactionInputDtoList = transactionInputDtoList;
    }

    public List<TransactionOutputDTO> getTransactionOutputDtoList() {
        return transactionOutputDtoList;
    }

    public void setTransactionOutputDtoList(List<TransactionOutputDTO> transactionOutputDtoList) {
        this.transactionOutputDtoList = transactionOutputDtoList;
    }

    //endregion
}