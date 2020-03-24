package com.xingkaichun.helloworldblockchain.model.transaction;


import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易输出
 */
@Data
public class TransactionOutput implements Serializable {

    /**
     * 交易输出的UUID，由用户产生，区块链系统进行唯一性校验、格式校验。
     * 这个值的构成应当足够简单去验证这个值是否是唯一的。
     * 当区块数据足够庞大时，用户节点只有最近一部分区块与UTXO数据，这时节点必须也可以校验它的唯一性。
     * 这里建议它的构成是时间戳+一串字符。
     * 最近的区块只包含最近产生的交易，因此只要有最近的区块就可校验它的唯一性。
     */
    private String transactionOutputUUID;
    //交易输出的地址
    private StringAddress stringAddress;
    //交易输出的金额
    private BigDecimal value;

}
