package com.xingkaichun.helloworldblockchain.core;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.NumberUtil;
import com.xingkaichun.helloworldblockchain.model.Block;
import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.model.transaction.TransactionOutput;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;


/**
 * 区块链数据库：该类用于区块链数据的持久化。
 */
@Data
public abstract class BlockChainDataBase {

    private Logger logger = LoggerFactory.getLogger(BlockChainDataBase.class);

    protected Incentive incentive ;
    protected Consensus consensus ;

    protected static Gson gson;

    //region 区块增加与删除
    /**
     * 新增区块: 在不允许删除区块链上的区块的情况下，将一个新的区块添加到区块链上。
     * 这是一个有些复杂的操作，需要考虑如下几点:
     * 新增区块本身的数据的正确性;
     * 新增的区块是否能够正确衔接到区块链的尾部;
     */
    public abstract boolean addBlock(Block block) throws Exception ;

    /**
     * 删除区块链的尾巴区块(最后一个区块)
     */
    public abstract Block removeTailBlock() throws Exception ;

    /**
     * 删除区块高度大于等于@blockHeight的的区块
     */
    public abstract void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) throws Exception ;
    //endregion

    //region 区块链提供的通用方法
    /**
     * 查找区块链上的最后一个区块
     */
    public abstract Block findTailBlock() throws Exception ;
    /**
     * 查找区块链上的最后一个区块，返回的区块不包含交易信息
     */
    public abstract Block findTailNoTransactionBlock() throws Exception ;
    /**
     * 获取区块链的长度
     */
    public abstract BigInteger obtainBlockChainHeight() throws Exception ;

    /**
     * 在区块链中根据 UTXO ID 查找UTXO
     * @param transactionOutputUUID UTXO ID
     */
    public abstract TransactionOutput findUtxoByUtxoUuid(String transactionOutputUUID) throws Exception ;

    /**
     * 在区块链中根据区块高度查找区块
     * @param blockHeight 区块高度
     */
    public abstract Block findBlockByBlockHeight(BigInteger blockHeight) throws Exception ;

    /**
     * 在区块链中根据区块高度查找【未存储交易信息】的区块
     * @param blockHeight 区块高度
     */
    public abstract Block findNoTransactionBlockByBlockHeight(BigInteger blockHeight) throws Exception ;

    /**
     * 根据区块Hash查找区块高度
     * @param blockHash 区块Hash
     */
    public abstract BigInteger findBlockHeightByBlockHash(String blockHash) throws Exception ;

    /**
     * 在区块链中根据交易ID查找交易
     * @param transactionUUID 交易ID
     */
    public abstract Transaction findTransactionByTransactionUuid(String transactionUUID) throws Exception ;
    //endregion

    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    public abstract boolean isBlockCanApplyToBlockChain(Block block) throws Exception ;

    /**
     * 校验交易是否可以被添加进下一个区块之中。
     * 如果校验的是奖励交易，则需要整个区块的信息，因此这个函数包含了两个参数：交易所在的区块、交易
     */
    public abstract boolean isTransactionCanAddToNextBlock(Block block, Transaction transaction) throws Exception ;

    /**
     * 根据地址查询未花费交易输出
     */
    public abstract List<TransactionOutput> querUnspendTransactionOuputListByAddress(StringAddress stringAddress,long from,long size) throws Exception ;

    /**
     * 根据交易高度查询交易
     */
    public abstract List<Transaction> queryTransactionByTransactionHeight(BigInteger from,BigInteger size) throws Exception ;

    /**
     * 根据地址查询交易输出
     */
    public abstract List<TransactionOutput> queryTransactionOuputListByAddress(StringAddress stringAddress,long from,long size) throws Exception ;

    //region 校验交易金额
    /**
     * 是否是一个合法的交易金额：这里用于限制交易金额的最大值、最小值、小数保留位置
     */
    public boolean isTransactionAmountLegal(BigDecimal transactionAmount) {
        try {
            if(transactionAmount == null){
                return false;
            }
            //校验交易金额最小值
            if(transactionAmount.compareTo(BlockChainCoreConstants.TRANSACTION_MIN_AMOUNT) < 0){
                return false;
            }
            //校验交易金额最大值
            if(transactionAmount.compareTo(BlockChainCoreConstants.TRANSACTION_MAX_AMOUNT) > 0){
                return false;
            }
            //校验小数位数
            long decimalPlaces = NumberUtil.decimalPlaces(transactionAmount);
            if(decimalPlaces > BlockChainCoreConstants.TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES){
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("校验金额方法出现异常，请检查。",e);
            return false;
        }
    }
    /**
     * 重载，见其它同名函数
     */
    public boolean isTransactionAmountLegal(String transactionAmount) {
        if(transactionAmount == null){
            return false;
        }
        if(!NumberUtil.isNumber(transactionAmount)){
            return false;
        }
        BigDecimal bigDecimalTransactionAmount = new BigDecimal(transactionAmount);
        return isTransactionAmountLegal(bigDecimalTransactionAmount);
    }
    //endregion

    /**
     * 校验交易文本大小是否合法：用来限制交易的文本大小，用于限制区块大小
     */
    public boolean isTransactionTextSizeLegal(Transaction transaction) {
        if(transaction == null){
            return false;
        }
        try {
            String stringTransaction = gson.toJson(transaction);
            return stringTransaction.length() <= BlockChainCoreConstants.TRANSACTION_TEXT_MAX_SIZE;
        } catch (Exception e) {
            logger.error("校验交易文本大小是否合法出现异常，请检查。",e);
            return false;
        }
    }

    /**
     * 校验区块含有的交易数量是否合法：用来限制区块含有的交易数量，用于限制区块大小
     */
    public boolean isBlcokTransactionSizeLegal(Block block) {
        if(block == null){
            return false;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null){
            return true;
        }
        return transactions.size() <= BlockChainCoreConstants.BLOCK_MAX_TRANSACTION_SIZE;
    }
}