package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.utils.LongUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 存放有关存储容量有关的常量，例如区块最大的存储容量，交易最大的存储容量
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TextSizeRestrictionTool {

    private final static Logger logger = LoggerFactory.getLogger(TextSizeRestrictionTool.class);


    //交易文本字符串最大长度值
    public final static long TRANSACTION_TEXT_MAX_SIZE = 1024;
    //区块存储容量限制
    public final static long BLOCK_TEXT_MAX_SIZE = 1024 * 1024;
    //区块最多含有的交易数量
    public final static long BLOCK_MAX_TRANSACTION_SIZE = BLOCK_TEXT_MAX_SIZE/TRANSACTION_TEXT_MAX_SIZE;
    //nonce最大值
    public final static long MAX_NONCE = Long.MAX_VALUE;
    //nonce最小值
    public final static long MIN_NONCE = 0;



    //region 校验存储容量
    /**
     * 校验区块的存储容量是否合法：用来限制区块所占存储空间的大小。
     */
    public static boolean isBlockStorageCapacityLegal(Block block) {
        //校验时间戳占用存储空间
        long timestamp = block.getTimestamp();
        if(String.valueOf(timestamp).length() != 13){
            logger.debug("区块校验失败：区块时间戳所占存储空间不正确。");
            return false;
        }

        //校验共识占用存储空间
        long nonce = block.getNonce();
        if(LongUtil.isLessThan(nonce, TextSizeRestrictionTool.MIN_NONCE)){
            return false;
        }
        if(LongUtil.isGreatThan(nonce, TextSizeRestrictionTool.MAX_NONCE)){
            return false;
        }

        //校验区块中的交易占用的存储空间
        //校验区块中交易的数量
        List<Transaction> transactions = block.getTransactions();
        long transactionsSize = transactions==null?0L:transactions.size();
        if(transactionsSize > TextSizeRestrictionTool.BLOCK_MAX_TRANSACTION_SIZE){
            logger.debug(String.format("区块数据异常，区块里包含的交易数量超过限制值%d。",
                    TextSizeRestrictionTool.BLOCK_MAX_TRANSACTION_SIZE));
            return false;
        }
        //校验每一笔交易占用的存储空间
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!isTransactionStorageCapacityLegal(transaction)){
                    logger.debug("交易数据异常，交易的容量非法。");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 校验交易的存储容量是否合法：用来限制交易的所占存储空间的大小。
     */
    public static boolean isTransactionStorageCapacityLegal(Transaction transaction) {
        if(transaction == null){
            logger.debug("交易数据异常，交易不能为空。");
            return false;
        }

        long timestamp = transaction.getTimestamp();
        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionOutput> outputs = transaction.getOutputs();

        //校验时间的长度
        if(String.valueOf(timestamp).length() != 13){
            logger.debug("交易校验失败：交易时间戳所占存储空间不正确。");
            return false;
        }

        //校验交易输入
        if(inputs != null){
            for(TransactionInput transactionInput:inputs){
                ScriptKey scriptKey = transactionInput.getScriptKey();
                if(calculateScriptTextSize(scriptKey)>500){
                    logger.debug("交易校验失败：交易输入脚本所占存储空间过大。");
                    return false;
                }
            }
        }

        //校验交易输出
        if(outputs != null){
            for(TransactionOutput transactionOutput:outputs){
                String address = transactionOutput.getAddress();
                if(address.length()<=20){
                    logger.debug("账户地址长度过短");
                    return false;
                }
                if(address.length()>=40){
                    logger.debug("账户地址长度过长");
                    return false;
                }

                long value = transactionOutput.getValue();
                if(calculateLongTextSize(value)>100){
                    logger.debug("交易校验失败：交易金额长度超过存储限制");
                    return false;
                }

                ScriptLock scriptLock = transactionOutput.getScriptLock();
                if(calculateScriptTextSize(scriptLock)>500){
                    logger.debug("交易校验失败：交易输出脚本所占存储空间过大。");
                    return false;
                }
            }
        }

        //校验整笔交易所占存储空间
        if(calculateTransactionTextSize(transaction) > TextSizeRestrictionTool.TRANSACTION_TEXT_MAX_SIZE){
            logger.debug("交易数据异常，交易所占存储空间太大。");
            return false;
        }
        return true;
    }
    //endregion



    //region 计算文本大小
    /**
     * 计算脚本长度
     */
    private static long calculateTransactionTextSize(Transaction transaction) {
        long size = 0;
        long timestamp = transaction.getTimestamp();
        size += String.valueOf(timestamp).length();
        List<TransactionInput> inputs = transaction.getInputs();
        size += calculateTransactionInputTextSize(inputs);
        List<TransactionOutput> outputs = transaction.getOutputs();
        size += calculateTransactionOutputTextSize(outputs);
        return size;
    }
    private static long calculateTransactionOutputTextSize(List<TransactionOutput> outputs) {
        long size = 0;
        if(outputs == null || outputs.size()==0){
            return size;
        }
        for(TransactionOutput transactionOutput:outputs){
            size += calculateTransactionOutputTextSize(transactionOutput);
        }
        return size;
    }
    private static long calculateTransactionOutputTextSize(TransactionOutput output) {
        long size = 0;
        if(output == null){
            return 0L;
        }
        String address = output.getAddress();
        size += address.length();
        long value = output.getValue();
        size += calculateLongTextSize(value);
        ScriptLock scriptLock = output.getScriptLock();
        size += calculateScriptTextSize(scriptLock);
        return size;
    }
    private static long calculateTransactionInputTextSize(List<TransactionInput> inputs) {
        long size = 0;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInput transactionInput:inputs){
            size += calculateTransactionInputTextSize(transactionInput);
        }
        return size;
    }
    private static long calculateTransactionInputTextSize(TransactionInput input) {
        long size = 0;
        if(input == null){
            return size;
        }
        TransactionOutput unspendTransactionOutput = input.getUnspendTransactionOutput();
        size += calculateTransactionOutputTextSize(unspendTransactionOutput);
        ScriptKey scriptKey = input.getScriptKey();
        size += calculateScriptTextSize(scriptKey);
        return size;
    }
    private static long calculateScriptTextSize(Script script) {
        long size = 0;
        if(script == null || script.size()==0){
            return size;
        }
        for(String scriptCode:script){
            size += scriptCode.length();
        }
        return size;
    }
    private static long calculateLongTextSize(long number){
        return String.valueOf(number).length();
    }
    //endregion
}
