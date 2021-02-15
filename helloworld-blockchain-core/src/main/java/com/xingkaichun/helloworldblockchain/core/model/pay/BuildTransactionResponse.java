package com.xingkaichun.helloworldblockchain.core.model.pay;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionOutputDTO;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BuildTransactionResponse {
    //是否构建交易成功
    private boolean buildTransactionSuccess;
    //构建失败的原因
    private String message;

    //交易Hash
    private String transactionHash;
    //交易手续费
    private long fee;

    //找零地址
    private String payerChangeAddress;
    //找零金额
    private long payerChangeValue;

    //交易输入
    private List<TransactionOutput> transactionInputList;
    //交易输出（不包含找零交易输出）
    private List<TransactionOutputDTO> transactionOutpuDtoList;

    //经过处理后的交易
    private TransactionDTO transactionDTO;


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

    public String getPayerChangeAddress() {
        return payerChangeAddress;
    }

    public void setPayerChangeAddress(String payerChangeAddress) {
        this.payerChangeAddress = payerChangeAddress;
    }

    public long getPayerChangeValue() {
        return payerChangeValue;
    }

    public void setPayerChangeValue(long payerChangeValue) {
        this.payerChangeValue = payerChangeValue;
    }

    public List<TransactionOutput> getTransactionInputList() {
        return transactionInputList;
    }

    public void setTransactionInputList(List<TransactionOutput> transactionInputList) {
        this.transactionInputList = transactionInputList;
    }

    public List<TransactionOutputDTO> getTransactionOutpuDtoList() {
        return transactionOutpuDtoList;
    }

    public void setTransactionOutpuDtoList(List<TransactionOutputDTO> transactionOutpuDtoList) {
        this.transactionOutpuDtoList = transactionOutpuDtoList;
    }

    public TransactionDTO getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(TransactionDTO transactionDTO) {
        this.transactionDTO = transactionDTO;
    }
}
