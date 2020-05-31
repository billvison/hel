package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.script.Script;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * 存放有关存储容量有关的常量，例如区块最大的存储容量，交易最大的存储容量
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TextSizeRestrictionUtil {

    private final static Logger logger = LoggerFactory.getLogger(TextSizeRestrictionUtil.class);


    //交易文本字符串最大长度值
    public final static long TRANSACTION_TEXT_MAX_SIZE = 1024;
    //区块存储容量限制
    public final static long BLOCK_TEXT_MAX_SIZE = 1000 * 1024;
    //区块最多含有的交易数量
    public final static long BLOCK_MAX_TRANSACTION_SIZE = BLOCK_TEXT_MAX_SIZE/TRANSACTION_TEXT_MAX_SIZE;
    //nonce最大值
    public final static BigInteger MAX_NONCE = new BigInteger(String.join("", Collections.nCopies(50, "9")));
    //nonce最小值
    public final static BigInteger MIN_NONCE = BigInteger.ZERO;



    //region 校验存储容量
    /**
     * 校验区块的存储容量是否合法：用来限制区块所占存储空间的大小。
     */
    public static boolean isBlockStorageCapacityLegal(Block block) {
        //校验时间戳占用存储空间
        long timestamp = block.getTimestamp();
        if(String.valueOf(timestamp).length()>20){
            return false;
        }

        //校验共识占用存储空间
        BigInteger nonce = new BigInteger(block.getConsensusValue());
        if(BigIntegerUtil.isLessThan(nonce, TextSizeRestrictionUtil.MIN_NONCE)){
            return false;
        }
        if(BigIntegerUtil.isGreatThan(nonce, TextSizeRestrictionUtil.MAX_NONCE)){
            return false;
        }

        //校验区块中的交易占用的存储空间
        //校验区块中交易的数量
        List<Transaction> transactions = block.getTransactions();
        long transactionsSize = transactions==null?0L:transactions.size();
        if(transactionsSize > TextSizeRestrictionUtil.BLOCK_MAX_TRANSACTION_SIZE){
            logger.debug(String.format("区块数据异常，区块里包含的交易数量超过限制值%d。",
                    TextSizeRestrictionUtil.BLOCK_MAX_TRANSACTION_SIZE));
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
        List<String> messages = transaction.getMessages();

        //校验时间的长度
        if(String.valueOf(timestamp).length()>20){
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
                StringAddress stringAddress = transactionOutput.getStringAddress();
                if(stringAddress.getValue().length()<=20){
                    logger.debug("钱包地址长度过短");
                    return false;
                }
                if(stringAddress.getValue().length()>=40){
                    logger.debug("钱包地址长度过长");
                    return false;
                }

                BigDecimal value = transactionOutput.getValue();
                if(calculateBigDecimalTextSize(value)>100){
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

        //校验附加消息
        if(messages != null){
            for(String message:messages){
                if(message == null || message.length()==0){
                    logger.debug("交易校验失败：附加消息不能为空。");
                    return false;
                }
                if(message.length()>100){
                    logger.debug("交易校验失败：附加消息所占存储空间太大。");
                    return false;
                }
            }
        }

        //校验整笔交易所占存储空间
        if(calculateTransactionTextSize(transaction) > TextSizeRestrictionUtil.TRANSACTION_TEXT_MAX_SIZE){
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
        long size = 0L;
        long timestamp = transaction.getTimestamp();
        size += String.valueOf(timestamp).length();
        TransactionType transactionType = transaction.getTransactionType();
        size += String.valueOf(transactionType.getCode()).length();
        List<TransactionInput> inputs = transaction.getInputs();
        size += calculateTransactionInputTextSize(inputs);
        List<TransactionOutput> outputs = transaction.getOutputs();
        size += calculateTransactionOutputTextSize(outputs);
        List<String> messages = transaction.getMessages();
        size += calculateMessageTextSize(messages);
        return size;
    }
    private static long calculateMessageTextSize(List<String> messages) {
        long size = 0L;
        if(messages == null || messages.size()==0){
            return 0L;
        }
        for(String message:messages){
            size += message.length();
        }
        return size;
    }
    private static long calculateTransactionOutputTextSize(List<TransactionOutput> outputs) {
        long size = 0L;
        if(outputs == null || outputs.size()==0){
            return size;
        }
        for(TransactionOutput transactionOutput:outputs){
            size += calculateTransactionOutputTextSize(transactionOutput);
        }
        return size;
    }
    private static long calculateTransactionOutputTextSize(TransactionOutput output) {
        long size = 0L;
        if(output == null){
            return 0L;
        }
        StringAddress stringAddress = output.getStringAddress();
        size += stringAddress.getValue().length();
        BigDecimal bigDecimal = output.getValue();
        size += calculateBigDecimalTextSize(bigDecimal);
        ScriptLock scriptLock = output.getScriptLock();
        size += calculateScriptTextSize(scriptLock);
        return size;
    }
    private static long calculateTransactionInputTextSize(List<TransactionInput> inputs) {
        long size = 0L;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInput transactionInput:inputs){
            size += calculateTransactionInputTextSize(transactionInput);
        }
        return size;
    }
    private static long calculateTransactionInputTextSize(TransactionInput input) {
        long size = 0L;
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
        long size = 0L;
        if(script == null || script.size()==0){
            return size;
        }
        for(String scriptCode:script){
            size += scriptCode.length();
        }
        return size;
    }
    private static long calculateBigDecimalTextSize(BigDecimal number){
        return number.toPlainString().length();
    }
    //endregion
}
