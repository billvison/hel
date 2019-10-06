package com.xingkaichun.blockchain.sdk;

import java.math.BigDecimal;
import java.util.List;

public class TransactionSdk {

    //交易输入
    private List<String> utxoIds;
    //交易转账方公钥
    private String senderPublicKey;
    //交易转账接收方
    private String receiverPublicKey;
    //转账金额
    private BigDecimal transferValue;
    //找零金额
    private BigDecimal changeValue;
    //交易手续费
    private BigDecimal feeValue;
    //交易签名
    private String sign;

    public TransactionSdk(List<String> utxoIds, String senderPublicKey, String receiverPublicKey, BigDecimal transferValue, BigDecimal changeValue, BigDecimal feeValue, String sign) throws Exception {
        this.utxoIds = utxoIds;
        this.senderPublicKey = senderPublicKey;
        this.receiverPublicKey = receiverPublicKey;
        this.transferValue = transferValue;
        this.changeValue = changeValue;
        this.feeValue = feeValue;
        this.sign = sign;
        if( utxoIds==null || utxoIds.size()==0 ){
            throw new RuntimeException("创建交易失败：输入的UTXO不能为空");
        }
        BigDecimal zero = new BigDecimal(1);
        if( transferValue.compareTo(zero) > 0){
            throw new RuntimeException("创建交易失败：转账金额必须大于零");
        }
        if( changeValue.compareTo(zero) > 0){
            throw new RuntimeException("创建交易失败：找零金额必须大于零");
        }
        if( feeValue.compareTo(zero) > 0){
            throw new RuntimeException("创建交易失败：交易手续费必须大于零");
        }
    }
}
