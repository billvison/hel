package com.xingkaichun.helloworldblockchain.core.model.wallet;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BuildTransactionResponse {
    //是否构建交易成功
    private boolean buildTransactionSuccess;
    //若失败，填写构建失败的原因
    private String message;

    //交易Hash
    private String transactionHash;
    //交易手续费
    private long fee;

    //交易输入
    private List<TransactionOutput> transactionInputs;

    //找零
    private TransactionOutput payerChangeTransactionOutput;
    //交易输出（不包含找零交易输出）
    private List<TransactionOutput> transactionOutputs;

    //构建后的完整交易
    private TransactionDto transaction;


    public boolean isBuildTransactionSuccess() {
        return buildTransactionSuccess;
    }

    public void setBuildTransactionSuccess(boolean buildTransactionSuccess) {
        this.buildTransactionSuccess = buildTransactionSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public TransactionOutput getPayerChangeTransactionOutput() {
        return payerChangeTransactionOutput;
    }

    public void setPayerChangeTransactionOutput(TransactionOutput payerChangeTransactionOutput) {
        this.payerChangeTransactionOutput = payerChangeTransactionOutput;
    }

    public List<TransactionOutput> getTransactionInputs() {
        return transactionInputs;
    }

    public void setTransactionInputs(List<TransactionOutput> transactionInputs) {
        this.transactionInputs = transactionInputs;
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public void setTransactionOutputs(List<TransactionOutput> transactionOutputs) {
        this.transactionOutputs = transactionOutputs;
    }

    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }

}
