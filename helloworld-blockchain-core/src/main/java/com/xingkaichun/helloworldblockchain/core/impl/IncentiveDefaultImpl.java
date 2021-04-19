package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public long reward(Block block) {
        long fee = getFee(block);
        long subsidy = getSubsidy(block);
        long total = subsidy + fee;
        return total;
    }

    /**
     * 系统给予的挖矿津贴：这是一个预知奖励金额的奖励
     */
    private long getSubsidy(Block block) {
        long height = block.getHeight();
        //第一个区块奖励给程序员邢开春，感谢其兢兢业业一年从无到有，创建了helloworldcoin。奖励数量为总数的10%
        if(height==1){
        //第二个区块预留给社区的发展的基金
            //TODO 奖励
            return 100000000;
        }else {
            return 100000000;
        }
    }

    /**
     * 交易手续费
     */
    private long getFee(Block block) {
        long fees = 0;
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(transaction.getTransactionType() == TransactionType.COINBASE){
                    continue;
                }else if(transaction.getTransactionType() == TransactionType.NORMAL){
                    long input = TransactionTool.getInputsValue(transaction);
                    long output = TransactionTool.getOutputsValue(transaction);
                    long fee = input - output;
                    fees += fee;
                }else{
                    throw new RuntimeException("不能识别的交易类型");
                }
            }
        }
        return fees;
    }
}
