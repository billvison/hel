package com.xingkaichun.helloworldblockchain.netcore.transport.dto;


import java.io.Serializable;
import java.util.List;

/**
 * 交易
 * 属性含义参考 com.xingkaichun.helloworldblockchain.core.bo.transaction.Transaction
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionDTO implements Serializable {

    //交易输入
    private List<TransactionInputDTO> inputs;
    //交易输出
    private List<TransactionOutputDTO> outputs;




    //region get set


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
//endregion
}