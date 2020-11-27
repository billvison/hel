package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public long mineAward(Block block) {
        long fees = getFees(block);
        long subsidyCoin = getSubsidy(block);
        long total = subsidyCoin + fees;
        return total;
    }

    /**
     * 固定奖励
     */
    private long getSubsidy(Block block) {
        long subsidy = GlobalSetting.MinerConstant.INIT_MINE_BLOCK_INCENTIVE_COIN_AMOUNT;
        long blockHeight = block.getHeight();
        if(LongUtil.isLessEqualThan(blockHeight,1)){
        }else {
            long height = block.getHeight();
            long multiple = (height-1) / 210000;
            while (multiple >= 1){
                subsidy = subsidy/2;
                --multiple;
            }
        }
        return subsidy;
    }

    /**
     * 交易手续费
     */
    private long getFees(Block block) {
        long fees = 0;
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(transaction.getTransactionType() == TransactionType.COINBASE){
                    continue;
                }
                if(transaction.getTransactionType() != TransactionType.NORMAL){
                    throw new RuntimeException("不能识别的交易类型");
                }
                long input = TransactionTool.getInputsValue(transaction);
                long output = TransactionTool.getOutputsValue(transaction);
                long fee = input - output;
                fees += fee;
            }
        }
        return fees;
    }
}
