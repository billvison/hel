package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存区块链
 *
 */
@Data
public abstract class MemoryBlockChain {
    private List<String> addUtxoList = new ArrayList<>();
    private List<String> deleteUtxoList = new ArrayList<>();

    abstract void fillWriteBatch(Block block) throws Exception ;

    protected void fillWriteBatch(Block block, boolean rollback) throws Exception {
        //UTXO信息
        List<Transaction> packingTransactionList = block.getTransactions();
        if(packingTransactionList!=null){
            for(Transaction transaction:packingTransactionList){
                ArrayList<TransactionInput> inputs = transaction.getInputs();
                if(inputs!=null){
                    for(TransactionInput txInput:inputs){
                        if(rollback){
                            //将用掉的UTXO回滚
                            addUtxoList.add(txInput.getUtxo().getTransactionOutputUUID());
                        } else {
                            deleteUtxoList.add(txInput.getUtxo().getTransactionOutputUUID());
                        }
                    }
                }
                ArrayList<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs!=null){
                    for(TransactionOutput output:outputs){
                        if(rollback){
                            //将新产生的UTXO回滚
                            deleteUtxoList.add(output.getTransactionOutputUUID());
                        } else {
                            addUtxoList.add(output.getTransactionOutputUUID());
                        }
                    }
                }
            }
        }
    }
}