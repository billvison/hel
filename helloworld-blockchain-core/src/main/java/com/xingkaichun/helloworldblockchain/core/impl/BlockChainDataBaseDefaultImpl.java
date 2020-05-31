package com.xingkaichun.helloworldblockchain.core.impl;

import com.google.common.primitives.Bytes;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.enums.BlockChainActionEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.core.tools.*;
import com.xingkaichun.helloworldblockchain.core.utils.*;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 区块链
 *
 * 注意这是一个线程不安全的实现。在并发的情况下，不保证功能的正确性。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainDataBaseDefaultImpl extends BlockChainDataBase {

    private Logger logger = LoggerFactory.getLogger(BlockChainDataBaseDefaultImpl.class);

    //region 变量
    private final static String BlockChain_DataBase_DirectName = "BlockChainDataBase";
    //区块链数据库
    private DB blockChainDB;

    //TODO 可以选择关闭区块浏览器的功能，若是关闭的话，则会节约很多的磁盘空间。

    //区块链高度key：它对应的值是区块链的高度
    private final static String BLOCK_CHAIN_HEIGHT_KEY = "B_C_H_K";
    //区块链中总的交易数量
    private final static String TOTAL_TRANSACTION_QUANTITY_KEY = "T_T_Q_K";
    //区块链中的交易序列号
    private final static String TRANSACTION_SEQUENCE_NUMBER_IN_BLOCKCHIAN_PREFIX_FLAG  = "T_S_N_I_B_P_F";
    //区块高度标识：存储区块链高度到区块的映射
    private final static String BLOCK_HEIGHT_PREFIX_FLAG = "B_H_P_F_";
    //区块高度标识：存储区块链高度到没有交易信息的区块的映射
    private final static String BLOCK_HEIGHT_MAP_NO_TRANSACTION_BLOCK_PREFIX_FLAG = "B_H_M_N_T_B_P_F_";
    //标识：存储区块链Hash到区块高度的映射
    private final static String BLOCK_HASH_BLOCK_HEIGHT_PREFIX_FLAG = "B_HA_B_H_P_F_";
    //交易标识：存储交易哈希到交易的映射
    private final static String TRANSACTION_HASH_PREFIX_FLAG = "T_U_P_F_";
    //交易输出标识：存储交易输出哈希到交易输出的映射
    private final static String TRANSACTION_OUTPUT_HASH_PREFIX_FLAG = "T_O_U_P_F_";
    //未花费的交易输出标识：存储未花费交易输出哈希到未花费交易输出的映射
    private final static String UNSPEND_TRANSACTION_OUTPUT_HASH_PREFIX_FLAG = "U_T_O_U_P_F_";
    //哈希标识：哈希(交易哈希、交易输出哈希)的前缀，这里希望系统中所有使用到的哈希都是不同的
    private final static String HASH_PREFIX_FLAG = "U_F_";
    //地址标识：存储地址到交易输出的映射
    private final static String ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "A_T_T_O_P_F_";
    //地址标识：存储地址到未花费交易输出的映射
    private final static String ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "A_T_U_T_O_P_F_";

    //钱包地址截止标记
    private final static String ADDRESS_END_FLAG = "#" ;

    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    //endregion

    //region 构造函数
    public BlockChainDataBaseDefaultImpl(String blockchainDataPath,Incentive incentive,Consensus consensus) throws Exception {
        this.blockChainDB = LevelDBUtil.createDB(new File(blockchainDataPath,BlockChain_DataBase_DirectName));
        this.incentive = incentive ;
        this.consensus = consensus ;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                blockChainDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
    //endregion

    //region 区块增加与删除
    @Override
    public boolean addBlock(Block block) throws Exception {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            boolean isBlockCanApplyToBlockChain = isBlockCanApplyToBlockChain(block);
            if(!isBlockCanApplyToBlockChain){
                return false;
            }
            WriteBatch writeBatch = createWriteBatch(block,BlockChainActionEnum.ADD_BLOCK);
            LevelDBUtil.write(blockChainDB,writeBatch);
            return true;
        }finally {
            writeLock.unlock();
        }
    }

    /**
     * 补充区块的属性
     */
    private void fillBlockPropertity(Block block) throws Exception {

        BigInteger transactionSequenceNumberInBlock = BigInteger.ZERO;
        BigInteger transactionSequenceNumberInBlockChain = queryTransactionQuantity();
        BigInteger blockHeight = block.getHeight();
        List<Transaction> transactions = block.getTransactions();
        BigInteger transactionQuantity = transactions==null?BigInteger.ZERO:BigInteger.valueOf(transactions.size());
        block.setTransactionQuantity(transactionQuantity);
        block.setStartTransactionSequenceNumberInBlockChain(
                BigIntegerUtil.isEquals(transactionQuantity,BigInteger.ZERO)?
                        BigInteger.ZERO:transactionSequenceNumberInBlockChain.add(BigInteger.ONE));
        block.setEndTransactionSequenceNumberInBlockChain(transactionSequenceNumberInBlockChain.add(transactionQuantity));
        for(Transaction transaction:transactions){
            transactionSequenceNumberInBlock = transactionSequenceNumberInBlock.add(BigInteger.ONE);
            transactionSequenceNumberInBlockChain = transactionSequenceNumberInBlockChain.add(BigInteger.ONE);
            transaction.setBlockHeight(blockHeight);
            transaction.setTransactionSequenceNumberInBlock(transactionSequenceNumberInBlock);
            transaction.setTransactionSequenceNumberInBlockChain(transactionSequenceNumberInBlockChain);

            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (int i=0; i <outputs.size(); i++){
                    TransactionOutput transactionOutput = outputs.get(i);
                    transactionOutput.setBlockHeight(blockHeight);
                    transactionOutput.setTransactionOutputSequence(BigInteger.valueOf(i).add(BigInteger.ONE));
                    transactionOutput.setTransactionSequenceNumberInBlock(transaction.getTransactionSequenceNumberInBlock());
                }
            }
        }
    }

    @Override
    public Block removeTailBlock() throws Exception {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            Block tailBlock = findTailBlock();
            if(tailBlock == null){
                return null;
            }
            WriteBatch writeBatch = createWriteBatch(tailBlock,BlockChainActionEnum.DELETE_BLOCK);
            LevelDBUtil.write(blockChainDB,writeBatch);
            return tailBlock;
        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) throws Exception {
        if(blockHeight == null){
            throw new NullPointerException("区块高度不能为空");
        }
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            while (true){
                Block tailBlock = findTailBlock();
                if(tailBlock == null){
                    return;
                }
                if(BigIntegerUtil.isLessThan(tailBlock.getHeight(),blockHeight)){
                    return;
                }
                WriteBatch writeBatch = createWriteBatch(tailBlock,BlockChainActionEnum.DELETE_BLOCK);
                LevelDBUtil.write(blockChainDB,writeBatch);
            }
        }finally {
            writeLock.unlock();
        }
    }
    //endregion


    //region 区块链提供的通用方法
    @Override
    public Block findTailBlock() throws Exception {
        BigInteger blockChainHeight = obtainBlockChainHeight();
        if(BigIntegerUtil.isLessEqualThan(blockChainHeight,BigInteger.valueOf(0))){
            return null;
        }
        return findBlockByBlockHeight(blockChainHeight);
    }
    @Override
    public Block findTailNoTransactionBlock() throws Exception {
        BigInteger blockChainHeight = obtainBlockChainHeight();
        if(BigIntegerUtil.isLessEqualThan(blockChainHeight,BigInteger.valueOf(0))){
            return null;
        }
        return findNoTransactionBlockByBlockHeight(blockChainHeight);
    }

    @Override
    public BigInteger obtainBlockChainHeight() throws Exception {
        byte[] bytesBlockChainHeight = LevelDBUtil.get(blockChainDB, buildBlockChainHeightKey());
        if(bytesBlockChainHeight == null){
            //区块链没有区块高度默认为0
            return BigInteger.valueOf(0);
        }
        return EncodeDecodeUtil.decodeToBigInteger(bytesBlockChainHeight);
    }

    @Override
    public TransactionOutput findUnspendTransactionOuputByTransactionOuputHash(String transactionOutputHash) throws Exception {
        if(transactionOutputHash==null || "".equals(transactionOutputHash)){
            return null;
        }
        byte[] bytesUtxo = LevelDBUtil.get(blockChainDB, buildUnspendTransactionOutputHashKey(transactionOutputHash));
        if(bytesUtxo == null){
            return null;
        }
        return EncodeDecodeUtil.decodeToTransactionOutput(bytesUtxo);
    }

    @Override
    public Block findBlockByBlockHeight(BigInteger blockHeight) throws Exception {
        byte[] bytesBlock = LevelDBUtil.get(blockChainDB, buildBlockHeightKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeUtil.decodeToBlock(bytesBlock);
    }
    @Override
    public Block findNoTransactionBlockByBlockHeight(BigInteger blockHeight) throws Exception {
        if(blockHeight == null){
            return null;
        }
        byte[] bytesBlock = LevelDBUtil.get(blockChainDB, buildBlockHeightMapNoTransactionBlockKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeUtil.decodeToBlock(bytesBlock);
    }
    @Override
    public BigInteger findBlockHeightByBlockHash(String blockHash) throws Exception {
        byte[] bytesBlockHeight = LevelDBUtil.get(blockChainDB, buildBlockHashtBlockHeightKey(blockHash));
        if(bytesBlockHeight == null){
            return null;
        }
        return new BigInteger(LevelDBUtil.bytesToString(bytesBlockHeight));
    }

    @Override
    public Transaction findTransactionByTransactionHash(String transactionHash) throws Exception {
        byte[] bytesTransaction = LevelDBUtil.get(blockChainDB, buildTransactionHashKey(transactionHash));
        if(bytesTransaction==null){
            return null;
        }
        return EncodeDecodeUtil.decodeToTransaction(bytesTransaction);
    }
    //endregion

    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    @Override
    public boolean isBlockCanApplyToBlockChain(@Nonnull Block block) throws Exception {
        //校验区块时间
        if(!isBlockTimestampLegal(block)){
            logger.debug("区块生成的时间太滞后。");
            return false;
        }

        //检查系统版本是否支持
        if(!GlobalSetting.SystemVersionConstant.isVersionLegal(block.getTimestamp())){
            logger.debug("系统版本过低，不支持校验区块，请尽快升级系统。");
            return false;
        }

        //校验区块的存储容量是否合法
        if(!TextSizeRestrictionUtil.isBlockStorageCapacityLegal(block)){
            logger.debug("区块存储容量非法。");
            return false;
        }

        //校验区块的连贯性
        if(!isBlockHashBlockHeightBlockTimestampRight(block)){
            logger.debug("区块校验失败：区块连贯性校验失败。");
            return false;
        }

        //校验区块写入的属性值
        if(!isBlockWriteRight(block)){
            logger.debug("区块校验失败：区块的属性写入值与实际计算结果不一致。");
            return false;
        }

        //双花校验
        if(isDoubleSpendAttackHappen(block)){
            logger.debug("区块数据异常，检测到双花攻击。");
            return false;
        }

        //校验主键的唯一性
        if(!isNewGenerateHashRight(block)){
            logger.debug("区块数据异常，区块中占用的部分主键已经被使用了。");
            return false;
        }

        //校验共识
        boolean isReachConsensus = consensus.isReachConsensus(this,block);
        if(!isReachConsensus){
            return false;
        }

        //激励校验
        if(!isIncentiveRight(block)){
            logger.debug("区块数据异常，激励异常。");
            return false;
        }

        //社区交易
        if(!isMaintenanceTransactionRight(block)){
            logger.debug("区块数据异常，社区维护的交易异常。");
            return false;
        }

        //从交易角度校验每一笔交易
        for(Transaction tx : block.getTransactions()){
            boolean transactionCanAddToNextBlock = isTransactionCanAddToNextBlock(block,tx);
            if(!transactionCanAddToNextBlock){
                logger.debug("区块数据异常，交易异常。");
                return false;
            }
        }
        return true;
    }


    private boolean isMaintenanceTransactionRight(Block block) {
        //社区维护的交易只能有一笔，也可以没有。
        List<Transaction> transactions = block.getTransactions();

        Transaction maintenanceTransaction = null;
        //校验社区维护交易有且只能有一笔
        //社区维护交易笔数
        int minerTransactionNumber = 0;
        if(transactions != null){
            for(Transaction transaction : transactions){
                if(transaction.getTransactionType() == TransactionType.COMMUNITY_MAINTENANCE){
                    minerTransactionNumber++;
                    maintenanceTransaction = transaction;
                }
            }
        }
        if(minerTransactionNumber > 1){
            logger.debug("区块数据异常，一个区块社区维护的交易只能有一笔。");
            return false;
        }

        //校验奖励交易
        if(!MaintenanceTransactionUtil.isMaintenanceTransactionRight(block.getTimestamp(),block.getHeight(),maintenanceTransaction)){
            logger.debug("交易校验失败：社区奖励交易验证失败。");
            return false;
        }
        return true;
    }


    /**
     * 是否有双花攻击
     */
    private boolean isDoubleSpendAttackHappen(Block block) {
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
     * 是否有双花攻击
     */
    private boolean isDoubleSpendAttackHappen(@Nonnull Transaction transaction) {
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
     * 校验激励
     */
    private boolean isIncentiveRight(Block block) throws Exception {
        //奖励交易有且只有一笔，且是区块的最后一笔交易
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            logger.debug("区块数据异常，没有检测到挖矿奖励交易。");
            return false;
        }
        for(int i=0; i<transactions.size()-1; i++){
            Transaction tx = transactions.get(i);
            if(tx.getTransactionType() == TransactionType.MINER_AWARD){
                logger.debug("区块数据异常，挖矿奖励应当是区块中最后一笔交易。");
                return false;
            }
        }
        Transaction transaction = transactions.get(transactions.size()-1);
        if(transaction.getTransactionType() != TransactionType.MINER_AWARD){
            logger.debug("区块数据异常，区块中最后一笔交易不是挖矿交易。");
            return false;
        }

        //校验奖励交易有且只能有一笔
        //挖矿交易笔数
        int minerTransactionNumber = 0;
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER_AWARD){
                minerTransactionNumber++;
            }
        }
        if(minerTransactionNumber == 0){
            logger.debug("区块数据异常，没有检测到挖矿奖励交易。");
            return false;
        }
        if(minerTransactionNumber > 1){
            logger.debug("区块数据异常，一个区块只能有一笔挖矿奖励。");
            return false;
        }

        //校验奖励交易
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER_AWARD){
                boolean transactionCanAddToNextBlock = isTransactionCanAddToNextBlock(block,tx);
                if(!transactionCanAddToNextBlock){
                    logger.debug("区块数据异常，激励交易异常。");
                    return false;
                }
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
    private boolean isNewGenerateHashRight(Block block) throws Exception {
        //校验区块Hash是否已经被使用了
        if(isHashExist(block.getHash())){
            logger.debug("区块数据异常，区块Hash已经被使用了。");
            return false;
        }
        //在不同的交易中，新生产的哈希(交易的哈希、交易输出哈希)不应该被使用两次或是两次以上
        Set<String> hashSet = new HashSet<>();
        for(Transaction transaction : block.getTransactions()){
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
        //校验每一笔交易新产生的Hash是否正确
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!isNewGenerateHashRight(transaction)){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isNewGenerateHashRight(Transaction transaction) {
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
        //交易哈希是否已经被使用了
        if(isHashExist(transactionHash)){
            return false;
        }
        //交易输出哈希是否已经被使用了
        if(outputs != null){
            for(TransactionOutput transactionOutput : outputs) {
                String transactionOutputHash = transactionOutput.getTransactionOutputHash();
                if(isHashExist(transactionOutputHash)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 简单的校验Block的连贯性:从高度、哈希、时间戳三个方面检查
     */
    private boolean isBlockHashBlockHeightBlockTimestampRight(Block block) throws Exception {
        Block tailBlock = findNoTransactionBlockByBlockHeight(obtainBlockChainHeight());
        if(tailBlock == null){
            //校验区块Hash是否连贯
            if(!GlobalSetting.GenesisBlockConstant.FIRST_BLOCK_PREVIOUS_HASH.equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度是否连贯
            if(!BigIntegerUtil.isEquals(GlobalSetting.GenesisBlockConstant.FIRST_BLOCK_HEIGHT,block.getHeight())){
                return false;
            }
        } else {
            //校验区块时间戳
            if(block.getTimestamp() <= tailBlock.getTimestamp()){
                return false;
            }
            //校验区块Hash是否连贯
            if(!tailBlock.getHash().equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度是否连贯
            if(!BigIntegerUtil.isEquals(tailBlock.getHeight().add(BigInteger.valueOf(1)),block.getHeight())){
                return false;
            }
        }
        return true;
    }

    /**
     * 区块中的某些属性是由其它属性计算得出，区块对象可能是由外部节点同步过来的。
     * 这里对区块对象中写入的属性值进行严格的校验，通过实际的计算一遍属性值与写入值进行比较，如果不同，则说明区块属性值不正确。
     */
    private boolean isBlockWriteRight(Block block) {
        //校验写入的可计算得到的值是否与计算得来的一致
        //校验交易的属性是否与计算得来的一致
        if(!BlockUtil.isBlockTransactionWriteRight(block)){
            return false;
        }
        //校验写入的MerkleRoot是否与计算得来的一致
        if(!BlockUtil.isBlockWriteMerkleRootRight(block)){
            return false;
        }
        //校验写入的Hash是否与计算得来的一致
        if(!BlockUtil.isBlockWriteHashRight(block)){
            return false;
        }
        return true;
    }

    @Override
    public boolean isTransactionCanAddToNextBlock(Block block, Transaction transaction) throws Exception{
        //校验交易类型
        TransactionType transactionType = transaction.getTransactionType();
        if(transactionType != TransactionType.NORMAL
                && transactionType != TransactionType.MINER_AWARD
                && transactionType != TransactionType.COMMUNITY_MAINTENANCE
        ){
            logger.debug("交易校验失败：不能识别的交易类型。");
            return false;
        }
        if(transactionType == TransactionType.MINER_AWARD){
            if(block == null){
                logger.debug("交易校验失败：验证激励交易必须区块参数不能为空。");
                return false;
            }
        }
        //业务校验
        //交易金额相关
        if(!isTransactionAmountLegal(transaction)){
            logger.debug("交易金额不合法");
            return false;
        }

        //校验交易存储
        if(!TextSizeRestrictionUtil.isTransactionStorageCapacityLegal(transaction)){
            logger.debug("请校验交易的大小");
            return false;
        }

        //校验交易的属性是否与计算得来的一致
        if(!BlockUtil.isTransactionWriteRight(block,transaction)){
            return false;
        }

        //验证交易时间
        if(!isTransactionTimestampLegal(block,transaction)){
            logger.debug("请校验交易的时间");
            return false;
        }

        //检查交易输入是否都是未花费交易输出
        if(!isUnspendTransactionOutput(transaction.getInputs())){
            logger.debug("区块数据异常：交易输入有不是未花费交易输出。");
            return false;
        }

        //校验：是否双花
        if(isDoubleSpendAttackHappen(transaction)){
            logger.debug("区块数据异常，检测到双花攻击。");
            return false;
        }

        //校验新产生的Hash是否可用
        if(!isNewGenerateHashRight(transaction)){
            logger.debug("校验数据异常，校验中占用的部分主键已经被使用了。");
            return false;
        }

        //根据交易类型，做进一步的校验
        if(transaction.getTransactionType() == TransactionType.MINER_AWARD){
            /**
             * 激励交易输出可以为空，这时代表矿工放弃了奖励、或者依据规则挖矿激励就是零奖励。
             */
            List<TransactionInput> inputs = transaction.getInputs();
            List<String> messages = transaction.getMessages();
            if(inputs != null && inputs.size()!=0){
                logger.debug("交易校验失败：激励交易不能有交易输入。");
                return false;
            }
            if(messages != null && messages.size()>0){
                logger.debug("交易校验失败：激励交易不能有附加信息。");
                return false;
            }
            if(!isBlockWriteMineAwardRight(block)){
                logger.debug("交易校验失败：挖矿交易的输出金额不正确。");
                return false;
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            /**
             * 普通交易输出可以为空，这时代表用户将自己的币扔进了黑洞，强制销毁了。
             */
            List<TransactionInput> inputs = transaction.getInputs();
            List<String> messages = transaction.getMessages();
            if(inputs == null || inputs.size()==0){
                logger.debug("交易校验失败：普通交易必须有交易输入。");
                return false;
            }
            if(messages != null && messages.size()>0){
                logger.debug("交易校验失败：普通交易不能有附加信息。");
                return false;
            }
            BigDecimal inputsValue = TransactionUtil.getInputsValue(transaction);
            BigDecimal outputsValue = TransactionUtil.getOutputsValue(transaction);
            if(inputsValue.compareTo(outputsValue) < 0) {
                logger.debug("交易校验失败：交易的输入少于交易的输出。不合法的交易。");
                return false;
            }
            //交易手续费
            if(inputsValue.subtract(outputsValue).compareTo(GlobalSetting.TransactionConstant.MIN_TRANSACTION_FEE)<0){
                logger.debug(String.format("交易校验失败：交易手续费不能小于%s。不合法的交易。", GlobalSetting.TransactionConstant.MIN_TRANSACTION_FEE));
                return false;
            }
            //脚本脚本
            try{
                if(!TransactionUtil.verifyScript(transaction)) {
                    logger.debug("交易校验失败：校验交易签名失败。不合法的交易。");
                    return false;
                }
            }catch (Exception e){
                logger.debug("交易校验失败：校验交易签名失败。不合法的交易。",e);
                return false;
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.COMMUNITY_MAINTENANCE){
            List<TransactionInput> inputs = transaction.getInputs();
            List<String> messages = transaction.getMessages();
            if(inputs != null && inputs.size()!=0){
                logger.debug("交易校验失败：社区奖励交易不能有交易输入。");
                return false;
            }
            if(messages != null && messages.size()>0){
                logger.debug("交易校验失败：社区奖励交易不能有附加信息。");
                return false;
            }
            if(!MaintenanceTransactionUtil.isMaintenanceTransactionRight(block.getTimestamp(),block.getHeight(),transaction)){
                logger.debug("交易校验失败：社区奖励交易验证失败。");
                return false;
            }
            return true;
        } else {
            logger.debug("区块数据异常，不能识别的交易类型。");
            return false;
        }
    }

    /**
     * 区块的时间是否合法
     */
    private boolean isBlockTimestampLegal(Block block) {
        if(block.getTimestamp() > System.currentTimeMillis()){
            return false;
        }
        return true;
    }

    /**
     * 交易的时间是否合法
     */
    private boolean isTransactionTimestampLegal(Block block, Transaction transaction) {
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
     * 检查交易输入是否都是未花费交易输出
     */
    private boolean isUnspendTransactionOutput(List<TransactionInput> inputs) throws Exception {
        //校验：交易输入是否是UTXO
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
                String unspendTransactionOutputHash = unspendTransactionOutput.getTransactionOutputHash();
                TransactionOutput tx = findUnspendTransactionOuputByTransactionOuputHash(unspendTransactionOutputHash);
                if(tx == null){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 区块中写入的挖矿奖励是否正确？
     * @param block 被校验挖矿奖励是否正确的区块
     * @return
     */
    private boolean isBlockWriteMineAwardRight(Block block){
        try {
            //校验奖励交易笔数
            int mineAwardTransactionCount = 0;
            for(Transaction tx : block.getTransactions()){
                if(tx.getTransactionType() == TransactionType.MINER_AWARD){
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
                if(tx.getTransactionType() == TransactionType.MINER_AWARD){
                    mineAwardTransaction = tx;
                    break;
                }
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
                blockWritedMineAward.add(output.getValue());
            }

            //目标挖矿奖励
            BigDecimal targetMineAward = incentive.mineAward(this, block);
            return targetMineAward.compareTo(blockWritedMineAward) >= 0 ;
        } catch (Exception e){
            logger.debug("区块数据异常，挖矿奖励交易不正确。");
            return false;
        }
    }

    //region 数据库相关
    //region 拼装数据库Key的值
    private byte[] buildBlockChainHeightKey() {
        String stringKey = BLOCK_CHAIN_HEIGHT_KEY;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildHashKey(String hash) {
        String stringKey = HASH_PREFIX_FLAG + hash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildBlockHeightKey(BigInteger blockHeight) {
        String stringKey = BLOCK_HEIGHT_PREFIX_FLAG + blockHeight;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildBlockHeightMapNoTransactionBlockKey(BigInteger blockHeight) {
        String stringKey = BLOCK_HEIGHT_MAP_NO_TRANSACTION_BLOCK_PREFIX_FLAG + blockHeight;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildBlockHashtBlockHeightKey(String blockHash) {
        String stringKey = BLOCK_HASH_BLOCK_HEIGHT_PREFIX_FLAG + blockHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildTransactionHashKey(String transactionHash) {
        String stringKey = TRANSACTION_HASH_PREFIX_FLAG + transactionHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildTransactionOutputHashKey(String transactionOutputHash) {
        String stringKey = TRANSACTION_OUTPUT_HASH_PREFIX_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildUnspendTransactionOutputHashKey(String transactionOutputHash) {
        String stringKey = UNSPEND_TRANSACTION_OUTPUT_HASH_PREFIX_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildAddressToTransactionOuputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getStringAddress().getValue();
        String transactionOutputHash = transactionOutput.getTransactionOutputHash();
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildAddressToTransactionOuputListKey(String address) {
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildAddressToUnspendTransactionOutputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getStringAddress().getValue();
        String transactionOutputHash = transactionOutput.getTransactionOutputHash();
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildAddressToUnspendTransactionOutputListKey(String address) {
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildTotalTransactionQuantityKey() {
        String stringKey = TOTAL_TRANSACTION_QUANTITY_KEY ;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildTransactionSequenceNumberInBlockChainKey(BigInteger transactionSequenceNumberInBlockChain) {
        String stringKey = TRANSACTION_SEQUENCE_NUMBER_IN_BLOCKCHIAN_PREFIX_FLAG + transactionSequenceNumberInBlockChain;
        return LevelDBUtil.stringToBytes(stringKey);
    }

    //endregion

    //region 拼装WriteBatch
    /**
     * 将区块信息组装成WriteBatch对象
     */
    private WriteBatch createWriteBatch(Block block, BlockChainActionEnum blockChainActionEnum) throws Exception {
        fillBlockPropertity(block);
        WriteBatch writeBatch = new WriteBatchImpl();
        fillWriteBatch(writeBatch,block,blockChainActionEnum);
        return writeBatch;
    }

    /**
     * 把区块信息组装进WriteBatch对象
     */
    private void fillWriteBatch(WriteBatch writeBatch, Block block, BlockChainActionEnum blockChainActionEnum) throws Exception {
        if(writeBatch == null){
            throw new NullPointerException("参数writeBatch没有初始化");
        }
        if(block == null){
            throw new NullPointerException("区块不能为空");
        }
        if(blockChainActionEnum == null){
            throw new NullPointerException("区块链动作不能为空");
        }
        //更新区块数据
        byte[] blockHeightKey = buildBlockHeightKey(block.getHeight());
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockHeightKey, EncodeDecodeUtil.encode(block));
        }else{
            writeBatch.delete(blockHeightKey);
        }
        //存储无交易信息的区块
        byte[] blockHeightMapNoTransactionBlockKey = buildBlockHeightMapNoTransactionBlockKey(block.getHeight());
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            List<Transaction> transactions = block.getTransactions();
            block.setTransactions(null);
            writeBatch.put(blockHeightMapNoTransactionBlockKey, EncodeDecodeUtil.encode(block));
            block.setTransactions(transactions);
        }else{
            writeBatch.delete(blockHeightMapNoTransactionBlockKey);
        }
        //更新交易数量
        BigInteger transactionQuantity = queryTransactionQuantity();
        byte[] totalTransactionQuantityKey = buildTotalTransactionQuantityKey();
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(totalTransactionQuantityKey, EncodeDecodeUtil.encode(transactionQuantity.add(BigInteger.valueOf(block.getTransactions().size()))));
        }else{
            writeBatch.put(totalTransactionQuantityKey, EncodeDecodeUtil.encode(transactionQuantity.subtract(BigInteger.valueOf(block.getTransactions().size()))));
        }
        //区块Hash到区块高度的映射
        byte[] blockHashBlockHeightKey = buildBlockHashtBlockHeightKey(block.getHash());
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockHashBlockHeightKey, LevelDBUtil.stringToBytes(String.valueOf(block.getHeight())));
        }else{
            writeBatch.delete(blockHashBlockHeightKey);
        }
        //更新区块链的高度
        byte[] blockChainHeightKey = buildBlockChainHeightKey();
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockChainHeightKey,EncodeDecodeUtil.encode(block.getHeight()));
        }else{
            writeBatch.put(blockChainHeightKey,EncodeDecodeUtil.encode(block.getHeight().subtract(BigInteger.valueOf(1))));
        }

        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                byte[] hashKey = buildHashKey(transaction.getTransactionHash());
                //更新交易数据
                byte[] transactionHashKey = buildTransactionHashKey(transaction.getTransactionHash());
                //更新区块链中的交易序列号数据
                byte[] transactionSequenceNumberInBlockChainKey = buildTransactionSequenceNumberInBlockChainKey(transaction.getTransactionSequenceNumberInBlockChain());
                if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                    writeBatch.put(hashKey, hashKey);
                    writeBatch.put(transactionHashKey, EncodeDecodeUtil.encode(transaction));
                    writeBatch.put(transactionSequenceNumberInBlockChainKey, EncodeDecodeUtil.encode(transaction));
                } else {
                    writeBatch.delete(hashKey);
                    writeBatch.delete(transactionHashKey);
                    writeBatch.delete(transactionSequenceNumberInBlockChainKey);
                }
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs!=null){
                    for(TransactionInput txInput:inputs){
                        //更新UTXO数据
                        TransactionOutput unspendTransactionOutput = txInput.getUnspendTransactionOutput();
                        byte[] unspendTransactionOutputHashKey = buildUnspendTransactionOutputHashKey(unspendTransactionOutput.getTransactionOutputHash());
                        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                            writeBatch.delete(unspendTransactionOutputHashKey);
                        } else {
                            writeBatch.put(unspendTransactionOutputHashKey, EncodeDecodeUtil.encode(unspendTransactionOutput));
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs!=null){
                    for(TransactionOutput output:outputs){
                        byte[] hashKey2 = buildHashKey(output.getTransactionOutputHash());
                        //更新所有的交易输出
                        byte[] transactionOutputHashKey = buildTransactionOutputHashKey(output.getTransactionOutputHash());
                        //更新UTXO数据
                        byte[] unspendTransactionOutputHashKey = buildUnspendTransactionOutputHashKey(output.getTransactionOutputHash());
                        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                            writeBatch.put(hashKey2, hashKey2);
                            writeBatch.put(transactionOutputHashKey, EncodeDecodeUtil.encode(output));
                            writeBatch.put(unspendTransactionOutputHashKey, EncodeDecodeUtil.encode(output));
                        } else {
                            writeBatch.delete(hashKey2);
                            writeBatch.delete(transactionOutputHashKey);
                            writeBatch.delete(unspendTransactionOutputHashKey);
                        }
                    }
                }
            }
        }

        addAboutAddressWriteBatch(writeBatch,block,blockChainActionEnum);
    }

    private void addAboutAddressWriteBatch(WriteBatch writeBatch, Block block, BlockChainActionEnum blockChainActionEnum) throws Exception {
        for(Transaction transaction : block.getTransactions()){
            if(transaction == null){
                return;
            }
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspendTransactionOutput();
                    byte[] addressToUnspendTransactionOutputListKey = buildAddressToUnspendTransactionOutputListKey(utxo);
                    if(blockChainActionEnum == BlockChainActionEnum.ADD_BLOCK){
                        writeBatch.delete(addressToUnspendTransactionOutputListKey);
                    }else{
                        writeBatch.put(addressToUnspendTransactionOutputListKey, EncodeDecodeUtil.encode(utxo));
                    }
                }
            }

            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (TransactionOutput transactionOutput:outputs){
                    byte[] addressToTransactionOutputListKey = buildAddressToTransactionOuputListKey(transactionOutput);
                    byte[] addressToUnspendTransactionOutputListKey = buildAddressToUnspendTransactionOutputListKey(transactionOutput);
                    if(blockChainActionEnum == BlockChainActionEnum.ADD_BLOCK){
                        byte[] byteTransactionOutput = EncodeDecodeUtil.encode(transactionOutput);
                        writeBatch.put(addressToTransactionOutputListKey,byteTransactionOutput);
                        writeBatch.put(addressToUnspendTransactionOutputListKey,byteTransactionOutput);
                    }else{
                        writeBatch.delete(addressToTransactionOutputListKey);
                    }
                }
            }
        }
    }

    @Override
    public List<TransactionOutput> queryUnspendTransactionOuputListByAddress(StringAddress stringAddress,long from,long size) throws Exception {
        List<TransactionOutput> transactionOutputList = new ArrayList<>();
        DBIterator iterator = blockChainDB.iterator();
        byte[] addressToUnspendTransactionOutputListKey = buildAddressToUnspendTransactionOutputListKey(stringAddress.getValue());
        int currentFrom = 0;
        int currentSize = 0;
        for (iterator.seek(addressToUnspendTransactionOutputListKey); iterator.hasNext(); iterator.next()) {
            byte[] byteKey = iterator.peekNext().getKey();
            if(Bytes.indexOf(byteKey,addressToUnspendTransactionOutputListKey) != 0){
                break;
            }
            byte[] byteValue = iterator.peekNext().getValue();
            if(byteValue == null || byteValue.length==0){
                continue;
            }
            currentFrom++;
            if(currentFrom>=from && currentSize<size){
                TransactionOutput transactionOutput = EncodeDecodeUtil.decodeToTransactionOutput(byteValue);
                transactionOutputList.add(transactionOutput);
                currentSize++;
            }
            if(currentSize>=size){
                break;
            }
        }
        return transactionOutputList;
    }

    @Override
    public List<Transaction> queryTransactionByTransactionHeight(BigInteger from,BigInteger size) throws Exception {
        List<Transaction> transactionList = new ArrayList<>();
        for(int i=0;BigIntegerUtil.isLessThan(BigInteger.valueOf(i),size);i++){
            byte[] byteTransaction = LevelDBUtil.get(blockChainDB,buildTransactionSequenceNumberInBlockChainKey(from.add(BigInteger.valueOf(i))));
            if(byteTransaction == null){
                break;
            }
            Transaction transaction = EncodeDecodeUtil.decodeToTransaction(byteTransaction);
            transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    public List<TransactionOutput> queryTransactionOuputListByAddress(StringAddress stringAddress,long from,long size) throws Exception {
        List<TransactionOutput> transactionOutputList = new ArrayList<>();
        DBIterator iterator = blockChainDB.iterator();
        byte[] addressToTransactionOutputListKey = buildAddressToTransactionOuputListKey(stringAddress.getValue());
        int currentFrom = 0;
        int currentSize = 0;
        for (iterator.seek(addressToTransactionOutputListKey); iterator.hasNext(); iterator.next()) {
            byte[] byteKey = iterator.peekNext().getKey();
            if(Bytes.indexOf(byteKey,addressToTransactionOutputListKey) != 0){
                break;
            }
            byte[] byteValue = iterator.peekNext().getValue();
            if(byteValue == null || byteValue.length==0){
                continue;
            }
            currentFrom++;
            if(currentFrom>=from && currentSize<size){
                TransactionOutput transactionOutput = EncodeDecodeUtil.decodeToTransactionOutput(byteValue);
                transactionOutputList.add(transactionOutput);
                currentSize++;
            }
            if(currentSize>=size){
                break;
            }
        }
        return transactionOutputList;
    }
    //endregion
    //endregion


    /**
     * 哈希是否已经存在于区块链之中？
     */
    private boolean isHashExist(String hash){
        byte[] bytesHash = LevelDBUtil.get(blockChainDB, buildHashKey(hash));
        return bytesHash != null;
    }

    /**
     * 将hash保存进Set
     * 如果Set里已经包含了hash，返回false
     * 否则，将hash保存进Set，返回true
     */
    private boolean saveHash(Set<String> hashSet, String hash) {
        if(hashSet.contains(hash)){
            return false;
        } else {
            hashSet.add(hash);
        }
        return true;
    }

    /**
     * 查询区块链中总的交易数量
     */
    private BigInteger queryTransactionQuantity() {
        byte[] byteTotalTransactionQuantity = LevelDBUtil.get(blockChainDB, buildTotalTransactionQuantityKey());
        if(byteTotalTransactionQuantity == null){
            return BigInteger.ZERO;
        }
        return EncodeDecodeUtil.decodeToBigInteger(byteTotalTransactionQuantity);
    }

    /**
     * 交易中的金额是否符合系统的约束
     */
    private boolean isTransactionAmountLegal(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                if(!isTransactionAmountLegal(output.getValue())){
                    logger.debug("交易金额不合法");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否是一个合法的交易金额：这里用于限制交易金额的最大值、最小值、小数保留位置
     */
    private boolean isTransactionAmountLegal(BigDecimal transactionAmount) {
        try {
            if(transactionAmount == null){
                logger.debug("交易金额不合法：交易金额不能为空");
                return false;
            }
            //校验交易金额最小值
            if(transactionAmount.compareTo(GlobalSetting.TransactionConstant.TRANSACTION_MIN_AMOUNT) < 0){
                logger.debug("交易金额不合法：交易金额不能小于系统默认交易金额最小值");
                return false;
            }
            //校验交易金额最大值
            if(transactionAmount.compareTo(GlobalSetting.TransactionConstant.TRANSACTION_MAX_AMOUNT) > 0){
                logger.debug("交易金额不合法：交易金额不能大于系统默认交易金额最大值");
                return false;
            }
            //校验小数位数
            long decimalPlaces = NumberUtil.obtainDecimalPlaces(transactionAmount);
            if(decimalPlaces > GlobalSetting.TransactionConstant.TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES){
                logger.debug("交易金额不合法：交易金额的小数位数过多，大于系统默认小说最高精度");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.debug("校验金额方法出现异常，请检查。",e);
            return false;
        }
    }
}