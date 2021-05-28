package com.xingkaichun.helloworldblockchain.netcore.transport.dto;


import java.io.Serializable;
import java.util.List;

/**
 * 交易
 * 属性含义参考
 * @see com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionDto implements Serializable {

    //交易输入
    private List<TransactionInputDto> inputs;
    //交易输出
    private List<TransactionOutputDto> outputs;




    //region get set


    public List<TransactionInputDto> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInputDto> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutputDto> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutputDto> outputs) {
        this.outputs = outputs;
    }
//endregion
}