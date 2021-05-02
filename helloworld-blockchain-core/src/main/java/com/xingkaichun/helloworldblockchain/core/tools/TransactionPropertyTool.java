package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
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
    public static boolean isWritePropertiesRight(Transaction transaction) {
        if(!isTransactionHashRight(transaction)){
            return false;
        }
        if(TransactionType.COINBASE == transaction.getTransactionType()){
            //写入的激励金额，在区块层面进行校验。
        }else if(TransactionType.NORMAL == transaction.getTransactionType()){
            //nothing
        }else {
            throw new RuntimeException("不支持的交易类型");
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
