package com.xingkaichun.blockchain.core.impl;


import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.Checker;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;

import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 区块校验者
 */
public class DefaultChecker implements Checker {

    /**
     * 检测区块
     */
    public boolean checkBlock(BlockChainCore blockChainCore, Block block) throws Exception {
        for(Transaction tx : block.getTransactions()){
            if(!checkTransaction(blockChainCore,tx)){
                return false;
            }
        }
        return false;
    }

    /**
     * 校验交易的合法性
     */
    public boolean checkTransaction(BlockChainCore blockChainCore, Transaction transaction) throws Exception{
        ArrayList<TransactionInput> inputs = transaction.getInputs();
        if(inputs==null||inputs.size()==0){
            System.out.println("交易校验失败：交易的输入不能为空。不合法的交易。");
            return false;
        }
        for(TransactionInput i : inputs) {
            if(i.getUtxo() ==null){
                System.out.println("交易校验失败：交易的输入UTXO不能为空。不合法的交易。");
                return false;
            }
            if(!blockChainCore.isUTXO(i.getUtxo().getTransactionOutputUUID())){
                System.out.println("交易校验失败：交易的输入不是UTXO。不合法的交易。");
                return false;
            }
        }
        Set<String> input_UTXO_Ids = new HashSet<>();
        for(TransactionInput i : inputs) {
            String utxoId = i.getUtxo().getTransactionOutputUUID();
            //校验 同一张钱不能使用两次
            if(input_UTXO_Ids.contains(utxoId)){
                System.out.println("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
                return false;
            }
            input_UTXO_Ids.add(utxoId);
        }
        ArrayList<TransactionOutput> outputs = transaction.getOutputs();
        if(inputs==null||inputs.size()==0){
            System.out.println("交易校验失败：交易的输出不能为空。不合法的交易。");
            return false;
        }
        for(TransactionOutput o : outputs) {
            if(o.getValue().compareTo(new BigDecimal(0))<=0){
                System.out.println("交易校验失败：交易的输出<=0。不合法的交易。");
                return false;
            }
        }
        BigDecimal inputsValue = TransactionUtil.getInputsValue(transaction);
        BigDecimal outputsValue = TransactionUtil.getOutputsValue(transaction);
        if(inputsValue.compareTo(outputsValue)<0) {
            System.out.println("交易校验失败：交易的输入少于交易的输出。不合法的交易。");
            return false;
        }
        //校验 付款方是同一个用户[公钥]
        PublicKeyString sender = TransactionUtil.getSender(transaction);
        for(TransactionInput i : inputs) {
            //校验 用户花的钱是自己的钱
            if(!i.getUtxo().getReciepient().getValue().equals(sender.getValue())){
                System.out.println("交易校验失败：交易的付款方有多个。不合法的交易。");
                return false;
            }
        }
        //校验签名验证
        try{
            if(!TransactionUtil.verifySignature(transaction)) {
                System.out.println("交易校验失败：校验交易签名失败。不合法的交易。");
                return false;
            }
        }catch (InvalidKeySpecException invalidKeySpecException){
            System.out.println("交易校验失败：校验交易签名失败。不合法的交易。");
            return false;
        }catch (Exception e){
            System.out.println("交易校验失败：校验交易签名失败。不合法的交易。");
            return false;
        }

        return true;
    }

}
