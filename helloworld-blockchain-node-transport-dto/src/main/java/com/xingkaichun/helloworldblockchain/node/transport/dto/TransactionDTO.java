package com.xingkaichun.helloworldblockchain.node.transport.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 交易
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
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
}
