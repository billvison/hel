package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import java.util.List;

/**
 *
 * @author xingkaichun@ceair.com
 */
public class UnconfirmedTransactionVo {

    private String transactionHash;
    private List<TransactionInputVo> inputs;
    private List<TransactionOutputVo> outputs;

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public List<TransactionInputVo> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInputVo> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutputVo> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOutputVo> outputs) {
        this.outputs = outputs;
    }

    public static class TransactionInputVo {
        private long value;
        private String address;
        private String transactionHash;
        private long transactionOutputIndex;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public void setTransactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
        }

        public long getTransactionOutputIndex() {
            return transactionOutputIndex;
        }

        public void setTransactionOutputIndex(long transactionOutputIndex) {
            this.transactionOutputIndex = transactionOutputIndex;
        }
    }

    public static class TransactionOutputVo {
        private long value;
        private String address;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
