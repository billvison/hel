package com.xingkaichun.helloworldblockchain.explorer.dto.transaction;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryMiningTransactionByTransactionHashResponse {

    private TransactionDto transactionDTO;

    //region get set

    public TransactionDto getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(TransactionDto transactionDTO) {
        this.transactionDTO = transactionDTO;
    }


    //endregion

    public static class TransactionDto{
        private String transactionHash;
        private List<QueryMiningTransactionListResponse.TransactionInputDto> inputs;
        private List<QueryMiningTransactionListResponse.TransactionOutputDto> outputs;

        public String getTransactionHash() {
            return transactionHash;
        }

        public void setTransactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
        }

        public List<QueryMiningTransactionListResponse.TransactionInputDto> getInputs() {
            return inputs;
        }

        public void setInputs(List<QueryMiningTransactionListResponse.TransactionInputDto> inputs) {
            this.inputs = inputs;
        }

        public List<QueryMiningTransactionListResponse.TransactionOutputDto> getOutputs() {
            return outputs;
        }

        public void setOutputs(List<QueryMiningTransactionListResponse.TransactionOutputDto> outputs) {
            this.outputs = outputs;
        }
    }

    public static class TransactionInputDto{
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

    public static class TransactionOutputDto{
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
