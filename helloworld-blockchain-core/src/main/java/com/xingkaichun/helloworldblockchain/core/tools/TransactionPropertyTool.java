package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

/**
 * Transaction工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionPropertyTool {


    /**
     * 校验交易的属性是否与计算得来的一致
     */
    public static boolean checkWriteProperties(Transaction transaction) {
        if(!checkTransactionWriteTransactionHash(transaction)){
            return false;
        }
        return true;
    }

    /**
     * 校验写入的交易哈希是否正确
     */
    public static boolean checkTransactionWriteTransactionHash(Transaction transaction) {
        String targetTransactionHash = TransactionTool.calculateTransactionHash(transaction);
        return StringUtil.isEquals(targetTransactionHash,transaction.getTransactionHash());
    }
}
