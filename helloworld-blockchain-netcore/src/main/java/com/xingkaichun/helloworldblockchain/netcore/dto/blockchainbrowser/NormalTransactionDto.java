package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */

import java.util.List;

public class NormalTransactionDto {

    private String privateKey ;
    private List<Output> outputs ;

    public static class Output{
        private String address;
        private String value;




        //region get set

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        //endregion
    }




    //region get set

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    //endregion
}
