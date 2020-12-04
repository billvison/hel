package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Transaction工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class TransactionPropertyTool {

    private static final Logger logger = LoggerFactory.getLogger(TransactionPropertyTool.class);


    /**
     * 校验交易的属性是否与计算得来的一致
     */
    public static boolean isWritePropertiesRight(Transaction transaction) {
        if(!isTransactionHashRight(transaction)){
            return false;
        }
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs == null || outputs.size()==0){
            return true;
        }
        return true;
    }

    /**
     * 校验交易的哈希是否正确
     */
    public static boolean isTransactionHashRight(Transaction transaction) {
        String targetTransactionHash = TransactionTool.calculateTransactionHash(transaction);
        return StringUtil.isEquals(targetTransactionHash,transaction.getTransactionHash());
    }
}
