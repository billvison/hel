package com.xingkaichun.helloworldblockchain.core.utils;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.script.ScriptMachine;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * 社区维护交易
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class MaintenanceTransactionUtil {

    public static Transaction transaction(long timestamp,BigInteger blockHeight){
        if(BigIntegerUtil.isEquals(blockHeight,new BigInteger("2"))){
            return block2Transaction(timestamp);
        }
        return null;
    }

    public static boolean isTransactionRight(long timestamp,BigInteger blockHeight,Transaction maintenanceTransaction){
        if(BigIntegerUtil.isEquals(blockHeight,new BigInteger("2"))){
            if(maintenanceTransaction == null){
                return false;
            }
            Transaction transaction = block2Transaction(timestamp);
            return transaction.getTransactionHash().equals(maintenanceTransaction.getTransactionHash());
        }
        return true;
    }

    public static Transaction block2Transaction(long timestamp){
        Transaction transaction = new Transaction();
        transaction.setTimestamp(timestamp);
        transaction.setTransactionType(TransactionType.COMMUNITY_MAINTENANCE);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        TransactionOutput output = new TransactionOutput();
        output.setStringAddress(new StringAddress("15w9BkGqCVB6Psy6117yBE8xwNSHfT95XX"));
        output.setValue(new BigDecimal("2000000000"));
        output.setScriptLock(ScriptMachine.createPayToClassicAddressOutputScript("15w9BkGqCVB6Psy6117yBE8xwNSHfT95XX"));
        output.setTransactionOutputHash(BlockchainHashUtil.calculateTransactionOutputHash(transaction,output));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(BlockchainHashUtil.calculateTransactionHash(transaction));
        return transaction;
    }
}
