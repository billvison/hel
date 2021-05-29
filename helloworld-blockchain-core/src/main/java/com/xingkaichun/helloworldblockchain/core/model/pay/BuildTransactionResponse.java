package com.xingkaichun.helloworldblockchain.core.model.pay;

import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDto;

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

    //找零
    private InnerTransactionOutput payerChange;


    //交易输入
    private List<TransactionOutput> transactionInputList;
    //交易输出（不包含找零交易输出）
    private List<InnerTransactionOutput> transactionOutputList;

    //经过处理后的交易
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

    public InnerTransactionOutput getPayerChange() {
        return payerChange;
    }

    public void setPayerChange(InnerTransactionOutput payerChange) {
        this.payerChange = payerChange;
    }

    public List<TransactionOutput> getTransactionInputList() {
        return transactionInputList;
    }

    public void setTransactionInputList(List<TransactionOutput> transactionInputList) {
        this.transactionInputList = transactionInputList;
    }

    public List<InnerTransactionOutput> getTransactionOutputList() {
        return transactionOutputList;
    }

    public void setTransactionOutputList(List<InnerTransactionOutput> transactionOutputList) {
        this.transactionOutputList = transactionOutputList;
    }

    public TransactionDto getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDto transaction) {
        this.transaction = transaction;
    }








    public static class InnerTransactionOutput {

        private long value;

        private OutputScript outputScript;

        private String address;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public OutputScript getOutputScript() {
            return outputScript;
        }

        public void setOutputScript(OutputScript outputScript) {
            this.outputScript = outputScript;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
