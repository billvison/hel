package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
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
