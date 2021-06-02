package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.setting.Setting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

import java.util.List;

/**
 * (区块、交易)结构工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class StructureTool {


    /**
     * 校验区块的结构
     */
    public static boolean checkBlockStructure(Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            LogUtil.debug("区块数据异常：区块中的交易数量为0。区块必须有一笔创世的交易。");
            return false;
        }
        //校验区块中交易的数量
        long transactionCount = BlockTool.getTransactionCount(block);
        if(transactionCount > Setting.BlockSetting.BLOCK_MAX_TRANSACTION_COUNT){
            LogUtil.debug(String.format("区块包含交易数量是[%s]超过限制[%s]。",transactionCount, Setting.BlockSetting.BLOCK_MAX_TRANSACTION_COUNT));
            return false;
        }
        for(int i=0; i<transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            if(i == 0){
                if(transaction.getTransactionType() != TransactionType.GENESIS){
                    LogUtil.debug("区块数据异常：区块第一笔交易必须是创世交易。");
                    return false;
                }
            }else {
                if(transaction.getTransactionType() != TransactionType.STANDARD){
                    LogUtil.debug("区块数据异常：区块非第一笔交易必须是标准交易。");
                    return false;
                }
            }
        }
        //校验交易的结构
        for(Transaction transaction:transactions){
            if(!checkTransactionStructure(transaction)){
                LogUtil.debug("交易数据异常：交易结构异常。");
                return false;
            }
        }
        return true;
    }
    /**
     * 校验交易的结构
     */
    public static boolean checkTransactionStructure(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.GENESIS){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                LogUtil.debug("交易数据异常：创世交易不能有交易输入。");
                return false;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null || outputs.size()!=1){
                LogUtil.debug("交易数据异常：创世交易有且只能有一笔交易输出。");
                return false;
            }
            return true;
        }else if(transaction.getTransactionType() == TransactionType.STANDARD){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs == null || inputs.size()<1){
                LogUtil.debug("交易数据异常：标准交易的交易输入数量至少是1。");
                return false;
            }
            return true;
        }else {
            LogUtil.debug("交易数据异常：不能识别的交易的类型。");
            return false;
        }
    }
}
