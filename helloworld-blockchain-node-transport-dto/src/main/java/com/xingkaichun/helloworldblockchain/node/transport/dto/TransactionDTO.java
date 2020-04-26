package com.xingkaichun.helloworldblockchain.node.transport.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 交易
 */
@Data
public class TransactionDTO implements Serializable {

    //交易时间戳
    private long timestamp;
    //交易输入
    private List<TransactionInputDTO> inputs;
    //交易输出
    private List<TransactionOutputDTO> outputs;

}
