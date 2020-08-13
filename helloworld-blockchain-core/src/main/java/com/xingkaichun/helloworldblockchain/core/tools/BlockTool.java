package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.MerkleTreeUtil;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 区块工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockTool {

    private final static Logger logger = LoggerFactory.getLogger(BlockTool.class);

    /**
     * 计算区块的Hash值
     * @param block 区块
     */
    public static String calculateBlockHash(Block block) {
        String input = block.getTimestamp()+block.getPreviousBlockHash()+block.getHeight()+block.getMerkleTreeRoot()+block.getNonce();
        byte[] sha256Digest = SHA256Util.digest(ByteUtil.stringToBytes(input));
        return HexUtil.bytesToHexString(sha256Digest) + block.getTimestamp();
    }

    /**
     * 区块中写入的默克尔树根是否正确
     */
    public static boolean isBlockWriteHashRight(Block block){
        String targetHash = calculateBlockHash(block);
        return targetHash.equals(block.getHash());
    }

    /**
     * 计算区块的默克尔树根值
     * @param block 区块
     */
    public static String calculateBlockMerkleTreeRoot(Block block) {
        List<Transaction> transactions = block.getTransactions();
        List<byte[]> hashList = new ArrayList<>();
        if(transactions != null){
            for(Transaction transaction : transactions) {
                hashList.add(ByteUtil.stringToBytes(transaction.getTransactionHash()));
            }
        }
        return MerkleTreeUtil.calculateBigEndianHexMerkleRoot(hashList);
    }
    /**
     * 区块中写入的默克尔树根是否正确
     */
    public static boolean isBlockWriteMerkleTreeRootRight(Block block){
        String targetMerkleRoot = calculateBlockMerkleTreeRoot(block);
        return targetMerkleRoot.equals(block.getMerkleTreeRoot());
    }

    /**
     * 校验交易的属性是否与计算得来的一致
     */
    public static boolean isBlockTransactionWriteRight(@Nonnull Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            return true;
        }
        for(Transaction transaction:transactions){
            if(!isTransactionWriteRight(block, transaction)){
                return false;
            }
        }
        return true;
    }

    /**
     * 校验交易的属性是否与计算得来的一致
     */
    public static boolean isTransactionWriteRight(Block block, @Nonnull Transaction transaction) {
        //校验挖矿交易的时间戳
        if(block != null){
            if(transaction.getTransactionType() == TransactionType.COINBASE){
                if(block.getTimestamp() != transaction.getTimestamp()){
                    return false;
                }
            }
        }
        if(!TransactionTool.isTransactionHashRight(transaction)){
            return false;
        }
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs == null || outputs.size()==0){
            return true;
        }
        for(TransactionOutput transactionOutput:outputs){
            if(!TransactionTool.isTransactionOutputHashRight(transaction,transactionOutput)){
                return false;
            }
        }
        return true;
    }

    /**
     * 校验区块中新产生的主键是否正确
     * 正确的条件是：
     * 主键不能已经被使用过了
     * 主键不能被连续使用一次以上
     * 主键符合约束
     */
    public static boolean isNewGenerateHashHappenTwiceAndMoreInnerBlock(Block block) {
        String blockHash = block.getHash();
        List<Transaction> blockTransactions = block.getTransactions();
        //在不同的交易中，新生产的哈希(区块的哈希、交易的哈希、交易输出哈希)不应该被使用两次或是两次以上
        Set<String> hashSet = new HashSet<>();
        if(!saveHash(hashSet,blockHash)){
            return false;
        }
        for(Transaction transaction : blockTransactions){
            String transactionHash = transaction.getTransactionHash();
            if(!saveHash(hashSet,transactionHash)){
                return false;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for(TransactionOutput transactionOutput : outputs) {
                    String transactionOutputHash = transactionOutput.getTransactionOutputHash();
                    if(!saveHash(hashSet,transactionOutputHash)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isNewGenerateHashHappenTwiceAndMoreInnerTransaction(Transaction transaction) {
        String transactionHash = transaction.getTransactionHash();
        //校验：只从交易对象层面校验，交易中新产生的哈希是否有重复
        Set<String> hashSet = new HashSet<>();
        if(!saveHash(hashSet,transactionHash)){
            return false;
        }
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput transactionOutput : outputs) {
                String transactionOutputHash = transactionOutput.getTransactionOutputHash();
                if(!saveHash(hashSet,transactionOutputHash)){
                    return false;
                }
            }
        }
        return true;
    }



    /**
     * 将hash保存进Set
     * 如果Set里已经包含了hash，返回false
     * 否则，将hash保存进Set，返回true
     */
    private static boolean saveHash(Set<String> hashSet, String hash) {
        if(hashSet.contains(hash)){
            return false;
        } else {
            hashSet.add(hash);
        }
        return true;
    }

    /**
     * 校验区块中交易类型的次序
     */
    public static boolean isBlockTransactionTypeRight(Block block) {
        List<Transaction> transactions = block.getTransactions();
        for(int i=0; i<transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            if(i == 0){
                if(transaction.getTransactionType() != TransactionType.COINBASE){
                    logger.debug("区块数据异常，区块第一笔交易必须是CoinBase。");
                    return false;
                }
            }else {
                if(transaction.getTransactionType() != TransactionType.NORMAL){
                    logger.debug("区块数据异常，区块非第一笔交易必须是普通交易。");
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 是否有双花攻击
     * TODO https://www.jianshu.com/p/d2b705a8c83f 解释双花
     */
    public static boolean isDoubleSpendAttackHappen(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs == null || inputs.size()==0){
            return false;
        }
        Set<String> hashSet = new HashSet<>();
        for(TransactionInput transactionInput : inputs) {
            TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
            String unspendTransactionOutputHash = unspendTransactionOutput.getTransactionOutputHash();
            if(hashSet.contains(unspendTransactionOutputHash)){
                return true;
            }
            hashSet.add(unspendTransactionOutputHash);
        }
        return false;
    }

    /**
     * 是否有双花攻击
     * TODO https://www.jianshu.com/p/d2b705a8c83f 解释双花
     */
    public static boolean isDoubleSpendAttackHappen(Block block) {
        //在不同的交易中，哈希(交易的哈希、交易输入哈希、交易输出哈希)不应该被使用两次或是两次以上
        Set<String> hashSet = new HashSet<>();
        for(Transaction transaction : block.getTransactions()){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for(TransactionInput transactionInput : inputs) {
                    TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
                    String unspendTransactionOutputHash = unspendTransactionOutput.getTransactionOutputHash();
                    if(hashSet.contains(unspendTransactionOutputHash)){
                        return true;
                    }
                    hashSet.add(unspendTransactionOutputHash);
                }
            }
        }
        return false;
    }


    /**
     * 区块中写入的挖矿奖励是否正确？
     * @param incentive 矿工挖矿激励
     * @param block 被校验挖矿奖励是否正确的区块
     */
    public static boolean isBlockWriteMineAwardRight(Incentive incentive, Block block){
        //校验奖励交易笔数
        int mineAwardTransactionCount = 0;
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.COINBASE){
                mineAwardTransactionCount++;
            }
        }
        if(mineAwardTransactionCount == 0){
            logger.debug("区块中没有奖励交易。");
            return false;
        }
        if(mineAwardTransactionCount > 1){
            logger.debug("区块中不能有两笔奖励交易。");
            return false;
        }

        //获取区块中写入的挖矿奖励交易
        Transaction mineAwardTransaction = null;
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.COINBASE){
                mineAwardTransaction = tx;
                break;
            }
        }

        if(mineAwardTransaction == null){
            logger.debug("区块中没有发现挖矿奖励交易。");
            return false;
        }
        List<TransactionInput> inputs = mineAwardTransaction.getInputs();
        if(inputs!=null && inputs.size()!=0){
            logger.debug("区块数据异常：挖矿交易的输入只能为空。");
            return false;
        }
        List<TransactionOutput> outputs = mineAwardTransaction.getOutputs();
        if(outputs == null){
            logger.debug("区块数据异常：挖矿交易的输出不能为空。");
            return false;
        }
        if(outputs.size() != 1){
            logger.debug("区块数据异常：挖矿交易的输出有且只能有一笔。");
            return false;
        }
        //校验正反
        for(TransactionOutput output:outputs){
            if(output.getValue().compareTo(BigDecimal.ZERO)<0){
                logger.debug("区块数据异常：挖矿交易的输出不能小于0。");
                return false;
            }
        }

        //获取区块中写入的挖矿奖励金额
        BigDecimal blockWritedMineAward = BigDecimal.ZERO;
        for(TransactionOutput output:outputs){
            blockWritedMineAward = blockWritedMineAward.add(output.getValue());
        }

        //目标挖矿奖励
        BigDecimal targetMineAward = incentive.mineAward(block);
        return targetMineAward.compareTo(blockWritedMineAward) >= 0 ;
    }


    /**
     * 区块的时间是否合法
     */
    public static boolean isBlockTimestampLegal(Block block) {
        if(block.getTimestamp() > System.currentTimeMillis()){
            return false;
        }
        return true;
    }

    /**
     * 交易的时间是否合法
     */
    public static boolean isTransactionTimestampLegal(Block block, Transaction transaction) {
        //校验交易的时间是否合理
        //交易的时间不能太滞后于当前时间
        if(transaction.getTimestamp() > System.currentTimeMillis() + GlobalSetting.MinerConstant.TRANSACTION_TIMESTAMP_MAX_AFTER_CURRENT_TIMESTAMP){
            logger.debug("交易校验失败：交易的时间戳太滞后了。");
            return false;
        }
        //校验交易时间戳
        if(block != null){
            //将区块放入区块链的时候，校验交易的逻辑
            //交易超前 区块生成时间
            if(transaction.getTimestamp() < block.getTimestamp() - GlobalSetting.MinerConstant.TRANSACTION_TIMESTAMP_MAX_BEFORE_CURRENT_TIMESTAMP){
                logger.debug("交易校验失败：交易的时间戳太老旧了。");
                return false;
            }
            //交易滞后 区块生成时间
            if(transaction.getTimestamp() > block.getTimestamp() + GlobalSetting.MinerConstant.TRANSACTION_TIMESTAMP_MAX_AFTER_CURRENT_TIMESTAMP){
                logger.debug("交易校验失败：交易的时间戳太老旧了。");
                return false;
            }
        }else {
            //挖矿时，校验交易的逻辑
            //交易超前 区块生成时间
            if(transaction.getTimestamp() < System.currentTimeMillis() - GlobalSetting.MinerConstant.TRANSACTION_TIMESTAMP_MAX_BEFORE_CURRENT_TIMESTAMP/2){
                logger.debug("交易校验失败：交易的时间戳太老旧了。");
                return false;
            }
            //交易滞后 区块生成时间
            if(transaction.getTimestamp() > System.currentTimeMillis() + GlobalSetting.MinerConstant.TRANSACTION_TIMESTAMP_MAX_AFTER_CURRENT_TIMESTAMP/2){
                logger.debug("交易校验失败：交易的时间戳太老旧了。");
                return false;
            }
        }
        return true;
    }



    /**
     * 区块中的某些属性是由其它属性计算得出，区块对象可能是由外部节点同步过来的。
     * 这里对区块对象中写入的属性值进行严格的校验，通过实际的计算一遍属性值与写入值进行比较，如果不同，则说明区块属性值不正确。
     */
    public static boolean isBlockWriteRight(Block block) {
        //校验写入的可计算得到的值是否与计算得来的一致
        //校验交易的属性是否与计算得来的一致
        if(!BlockTool.isBlockTransactionWriteRight(block)){
            return false;
        }
        //校验写入的MerkleRoot是否与计算得来的一致
        if(!BlockTool.isBlockWriteMerkleTreeRootRight(block)){
            return false;
        }
        //校验写入的Hash是否与计算得来的一致
        if(!BlockTool.isBlockWriteHashRight(block)){
            return false;
        }
        return true;
    }


}
