package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.core.utils.BlockChainCoreConstant;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    private Logger logger = LoggerFactory.getLogger(IncentiveDefaultImpl.class);

    @Override
    public BigDecimal mineAward(BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        //转账手续费
        BigDecimal fees = getFees(block);
        //区块固定奖励
        BigDecimal mixedCoin = BlockChainCoreConstant.INIT_MINE_BLOCK_INCENTIVE_COIN_AMOUNT;

        BigInteger blockHeight = block.getHeight();
        if(BigIntegerUtil.isLessEqualThan(blockHeight,BigInteger.ONE)){
        }else {
            Block firstBlock = blockChainDataBase.findBlockByBlockHeight(BigInteger.valueOf(1));
            long timestamp = BlockChainCoreConstant.MINE_BLOCK_INCENTIVE_REDUCE_BY_HALF_INTERVAL_TIMESTAMP;
            long totalTimestamp = System.currentTimeMillis() - firstBlock.getTimestamp();
            long multiple = totalTimestamp / timestamp;
            while (multiple > 1){
                mixedCoin = mixedCoin.divide(new BigDecimal("2"));
                --multiple;
            }
        }
        BigDecimal total = mixedCoin.add(fees);
        //小数位数
        BigDecimal setScaleTotal = total.setScale(BlockChainCoreConstant.TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES,BigDecimal.ROUND_DOWN);
        return setScaleTotal;
    }

    private BigDecimal getFees(Block block) {
        List<Transaction> transactions = block.getTransactions();
        BigDecimal fees = BigDecimal.ZERO;
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(transaction.getTransactionType() == TransactionType.MINER){
                    continue;
                }
                BigDecimal input = BigDecimal.ZERO;
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput txInput:inputs){
                        input = input.add(txInput.getUnspendTransactionOutput().getValue());
                    }
                }
                BigDecimal output = BigDecimal.ZERO;
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput txOutput:outputs){
                        output = output.add(txOutput.getValue());
                    }
                }
                BigDecimal fee = input.subtract(output);
                fees = fees.add(fee);
            }
        }
        return fees;
    }
}
