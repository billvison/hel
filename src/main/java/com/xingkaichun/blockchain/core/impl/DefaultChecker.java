package com.xingkaichun.blockchain.core.impl;


import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.Checker;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
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
        //区块角度检测区块的数据的安全性
        //同一张钱不能被两次交易同时使用【同一个UTXO在不同的交易中出现】
        Set<String> transactionOutputUUIDSet = new HashSet<>();
        //同一区块挖矿奖励交易只能有一次
        int minerTransactionTimes = 0;

        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER){
                minerTransactionTimes++;
                //有多个挖矿交易
                if(minerTransactionTimes>1){
                    throw new BlockChainCoreException("区块数据异常，一个区块只能有一笔挖矿奖励。");
                }
                checkMinerTransaction(tx);
            } else if(tx.getTransactionType() == TransactionType.NORMAL){
                checkTransaction(blockChainCore,tx);
                ArrayList<TransactionInput> inputs = tx.getInputs();
                for(TransactionInput input:inputs){
                    String transactionOutputUUID = input.getUtxo().getTransactionOutputUUID();
                    //同一个UTXO被多次使用
                    if(transactionOutputUUIDSet.contains(transactionOutputUUID)){
                        throw new BlockChainCoreException("区块数据异常，同一个UTXO在一个区块中多次使用。");
                    }
                    transactionOutputUUIDSet.add(transactionOutputUUID);
                }
            }
        }
        if(minerTransactionTimes == 0){
            throw new BlockChainCoreException("区块数据异常，没有检测到挖矿奖励交易。");
        }
        return true;
    }

    private void checkMinerTransaction(Transaction tx) {
    }

    /**
     * 校验交易的合法性
     */
    public boolean checkTransaction(BlockChainCore blockChainCore, Transaction transaction) throws Exception{
        ArrayList<TransactionInput> inputs = transaction.getInputs();
        if(inputs==null||inputs.size()==0){
            throw new BlockChainCoreException("交易校验失败：交易的输入不能为空。不合法的交易。");
        }
        for(TransactionInput i : inputs) {
            if(i.getUtxo() ==null){
                throw new BlockChainCoreException("交易校验失败：交易的输入UTXO不能为空。不合法的交易。");
            }
            if(!blockChainCore.isUTXO(i.getUtxo().getTransactionOutputUUID())){
                throw new BlockChainCoreException("交易校验失败：交易的输入不是UTXO。不合法的交易。");
            }
        }
        Set<String> input_UTXO_Ids = new HashSet<>();
        for(TransactionInput i : inputs) {
            String utxoId = i.getUtxo().getTransactionOutputUUID();
            //校验 同一张钱不能使用两次
            if(input_UTXO_Ids.contains(utxoId)){
                throw new BlockChainCoreException("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
            }
            input_UTXO_Ids.add(utxoId);
        }
        ArrayList<TransactionOutput> outputs = transaction.getOutputs();
        if(inputs==null||inputs.size()==0){
            throw new BlockChainCoreException("交易校验失败：交易的输出不能为空。不合法的交易。\"");
        }
        for(TransactionOutput o : outputs) {
            if(o.getValue().compareTo(new BigDecimal(0))<=0){
                throw new BlockChainCoreException("交易校验失败：交易的输出<=0。不合法的交易。");
            }
        }
        BigDecimal inputsValue = TransactionUtil.getInputsValue(transaction);
        BigDecimal outputsValue = TransactionUtil.getOutputsValue(transaction);
        if(inputsValue.compareTo(outputsValue)<0) {
            throw new BlockChainCoreException("交易校验失败：交易的输入少于交易的输出。不合法的交易。");
        }
        //校验 付款方是同一个用户[公钥]
        PublicKeyString sender = TransactionUtil.getSender(transaction);
        for(TransactionInput i : inputs) {
            //校验 用户花的钱是自己的钱
            if(!i.getUtxo().getReciepient().getValue().equals(sender.getValue())){
                throw new BlockChainCoreException("交易校验失败：交易的付款方有多个。不合法的交易。");
            }
        }
        //校验签名验证
        try{
            if(!TransactionUtil.verifySignature(transaction)) {
                throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
            }
        }catch (InvalidKeySpecException invalidKeySpecException){
            throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
        }catch (Exception e){
            throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
        }
        return true;
    }

}
