package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.core.utils.LongUtil;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    private final static Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public BigDecimal mineAward(Block block) {
        BigDecimal fees = getFees(block);
        BigDecimal subsidyCoin = getSubsidy(block);
        BigDecimal total = subsidyCoin.add(fees);
        return total;
    }

    /**
     * 固定奖励
     */
    private BigDecimal getSubsidy(Block block) {
        BigDecimal subsidy = GlobalSetting.MinerConstant.INIT_MINE_BLOCK_INCENTIVE_COIN_AMOUNT;
        long blockHeight = block.getHeight();
        if(LongUtil.isLessEqualThan(blockHeight,LongUtil.ONE)){
        }else {
            long height = block.getHeight();
            long multiple = (height-1) / 210000;
            while (multiple >= 1){
                subsidy = subsidy.divide(new BigDecimal("2"));
                //小数位数
                subsidy = subsidy.setScale(GlobalSetting.TransactionConstant.TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES,BigDecimal.ROUND_DOWN);
                --multiple;
            }
        }
        return subsidy;
    }

    /**
     * 交易手续费
     */
    private BigDecimal getFees(Block block) {
        BigDecimal fees = BigDecimal.ZERO;
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(transaction.getTransactionType() == TransactionType.COINBASE){
                    continue;
                }
                if(transaction.getTransactionType() != TransactionType.NORMAL){
                    throw new RuntimeException("不能识别的交易类型");
                }
                BigDecimal input = TransactionTool.getInputsValue(transaction);
                BigDecimal output = TransactionTool.getOutputsValue(transaction);
                BigDecimal fee = input.subtract(output);
                fees = fees.add(fee);
            }
        }
        return fees;
    }
}
