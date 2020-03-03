package com.xingkaichun.helloworldblockchain.node.dto.blockchain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class NormalTransactionDto {

    private String privateKey ;
    private List<Input> inputs ;
    private List<Output> outputs ;

    @Data
    public static class Output{
        private String address;
        private BigDecimal value;
    }

    @Data
    public static class Input{
        private String utxoUuid;
        private String publicKey;
    }
}
