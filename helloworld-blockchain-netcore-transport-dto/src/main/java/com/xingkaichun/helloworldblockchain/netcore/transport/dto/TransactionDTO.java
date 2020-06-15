package com.xingkaichun.helloworldblockchain.netcore.transport.dto;


import java.io.Serializable;
import java.util.List;

/**
 * 交易
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TransactionDTO implements Serializable {

    //交易时间戳
    private long timestamp;
    //交易类型代码
    private int transactionTypeCode;
    //交易输入
    private List<TransactionInputDTO> inputs;
    //交易输出
    private List<TransactionOutputDTO> outputs;
    //附加消息
    private List<String> messages;




    //region get set

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTransactionTypeCode() {
        return transactionTypeCode;
    }

    public void setTransactionTypeCode(int transactionTypeCode) {
        this.transactionTypeCode = transactionTypeCode;
    }

    public List<TransactionInputDTO> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInputDTO> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutputDTO> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutputDTO> outputs) {
        this.outputs = outputs;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    //endregion
}