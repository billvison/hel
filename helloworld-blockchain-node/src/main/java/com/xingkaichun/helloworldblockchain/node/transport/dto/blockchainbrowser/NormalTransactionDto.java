package com.xingkaichun.helloworldblockchain.node.transport.dto.blockchainbrowser;

import lombok.Data;

import java.util.List;

@Data
public class NormalTransactionDto {

    private String privateKey ;
    private List<String> inputs ;
    private List<Output> outputs ;

    @Data
    public static class Output{
        private String address;
        private String value;
    }
}
