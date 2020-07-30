package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * 社区维护交易
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class CommunityMaintenanceTransactionTool {

    public static Transaction obtainMaintenanceTransaction(long timestamp, BigInteger blockHeight){
        if(BigIntegerUtil.isEquals(blockHeight,new BigInteger("2"))){
            return block2MaintenanceTransaction(timestamp);
        }
        return null;
    }

    public static boolean isMaintenanceTransactionRight(long timestamp, BigInteger blockHeight, Transaction maintenanceTransaction){
        if(BigIntegerUtil.isEquals(blockHeight,new BigInteger("2"))){
            if(maintenanceTransaction == null){
                return false;
            }
            Transaction transaction = block2MaintenanceTransaction(timestamp);
            return transaction.getTransactionHash().equals(maintenanceTransaction.getTransactionHash());
        }
        return true;
    }

    private static Transaction block2MaintenanceTransaction(long timestamp){
        Transaction transaction = new Transaction();
        transaction.setTimestamp(timestamp);
        transaction.setTransactionType(TransactionType.COMMUNITY_MAINTENANCE);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        TransactionOutput output = new TransactionOutput();
        output.setAddress("1F7cCJVfRoogxx32xUyGP5oGfRpDUthPed");
        output.setValue(new BigDecimal("2000000000"));
        output.setScriptLock(StackBasedVirtualMachine.createPayToClassicAddressOutputScript("1F7cCJVfRoogxx32xUyGP5oGfRpDUthPed"));
        output.setTransactionOutputHash(TransactionTool.calculateTransactionOutputHash(transaction,output));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transaction));
        return transaction;
    }
}
