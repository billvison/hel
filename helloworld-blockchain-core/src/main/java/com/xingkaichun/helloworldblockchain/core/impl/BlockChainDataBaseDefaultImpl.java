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
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.TextSizeRestrictionTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.core.utils.EncodeDecodeUtil;
import com.xingkaichun.helloworldblockchain.core.utils.LevelDBUtil;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 区块链
 *
 * 注意这是一个线程不安全的实现。在并发的情况下，不保证功能的正确性。
 * TODO 改善型功能 可以选择关闭区块浏览器的功能，若是关闭的话，则会节约很多的磁盘空间。
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainDataBaseDefaultImpl extends BlockChainDataBase {

    //region 变量与构造函数
    private final static Logger logger = LoggerFactory.getLogger(BlockChainDataBaseDefaultImpl.class);

    private final static String BlockChain_DataBase_DirectName = "BlockChainDataBase";
    //区块链数据库
    private DB blockChainDB;

    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public BlockChainDataBaseDefaultImpl(String blockchainDataPath,Incentive incentive,Consensus consensus) {
        super(consensus,incentive);
        File blockChainDBFile = new File(blockchainDataPath,BlockChain_DataBase_DirectName);
        this.blockChainDB = LevelDBUtil.createDB(blockChainDBFile);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LevelDBUtil.closeDB(blockChainDB);
        }));
    }
    //endregion



    //region 区块增加与删除
    @Override
    public boolean addBlock(Block block) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            boolean isBlockCanAddToBlockChain = isBlockCanAddToBlockChain(block);
            if(!isBlockCanAddToBlockChain){
                return false;
            }
            WriteBatch writeBatch = createWriteBatch(block,BlockChainActionEnum.ADD_BLOCK);
            LevelDBUtil.write(blockChainDB,writeBatch);
            return true;
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public void removeTailBlock() {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            Block tailBlock = queryTailBlock();
            if(tailBlock == null){
                return;
            }
            WriteBatch writeBatch = createWriteBatch(tailBlock,BlockChainActionEnum.DELETE_BLOCK);
            LevelDBUtil.write(blockChainDB,writeBatch);
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public void removeTailBlocksUtilBlockHeightLessThan(BigInteger blockHeight) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            while (true){
                Block tailBlock = queryTailBlock();
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



    //region 校验区块、交易
    @Override
    public boolean isBlockCanAddToBlockChain(Block block) {
        //校验区块时间
        if(!BlockTool.isBlockTimestampLegal(block)){
            logger.debug("区块生成的时间太滞后。");
            return false;
        }

        //检查系统版本是否支持
        if(!GlobalSetting.SystemVersionConstant.isVersionLegal(block.getTimestamp())){
            logger.debug("系统版本过低，不支持校验区块，请尽快升级系统。");
            return false;
        }

        //校验区块的存储容量是否合法
        if(!TextSizeRestrictionTool.isBlockStorageCapacityLegal(block)){
            logger.debug("区块存储容量非法。");
            return false;
        }

        //校验区块的连贯性
        if(!isBlockHashBlockHeightBlockTimestampRight(block)){
            logger.debug("区块校验失败：区块连贯性校验失败。");
            return false;
        }

        //校验区块写入的属性值
        if(!BlockTool.isBlockWriteRight(block)){
            logger.debug("区块校验失败：区块的属性写入值与实际计算结果不一致。");
            return false;
        }

        //双花校验
        if(BlockTool.isDoubleSpendAttackHappen(block)){
            logger.debug("区块数据异常，检测到双花攻击。");
            return false;
        }

        //校验哈希作为主键的正确性
        //新产生的Hash不能有重复
        if(!BlockTool.isNewGenerateHashHappenTwiceAndMoreInnerBlock(block)){
            logger.debug("区块数据异常，区块中占用的部分主键已经被使用了。");
            return false;
        }
        //新产生的Hash不能有已经被使用过的
        if(!isNewGenerateHashNeverHappenInBlockchain(block)){
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

        //校验交易类型的次序
        if(!BlockTool.isBlockTransactionTypeRight(block)){
            logger.debug("区块数据异常，区块有且只有一笔交易是CoinBase，且CoinBase交易是区块的第一笔交易。");
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
    @Override
    public boolean isTransactionCanAddToNextBlock(Block block, Transaction transaction) {
        //校验交易类型
        TransactionType transactionType = transaction.getTransactionType();
        if(transactionType != TransactionType.NORMAL
                && transactionType != TransactionType.COINBASE)
        {
            logger.debug("交易校验失败：不能识别的交易类型。");
            return false;
        }
        if(transactionType == TransactionType.COINBASE){
            if(block == null){
                logger.debug("交易校验失败：验证激励交易必须区块参数不能为空。");
                return false;
            }
        }
        //业务校验
        //交易金额相关
        if(!TransactionTool.isTransactionAmountLegal(transaction)){
            logger.debug("交易金额不合法");
            return false;
        }

        //校验交易存储
        if(!TextSizeRestrictionTool.isTransactionStorageCapacityLegal(transaction)){
            logger.debug("请校验交易的大小");
            return false;
        }

        //校验交易的属性是否与计算得来的一致
        if(!BlockTool.isTransactionWriteRight(block,transaction)){
            return false;
        }

        //验证交易时间
        if(!BlockTool.isTransactionTimestampLegal(block,transaction)){
            logger.debug("请校验交易的时间");
            return false;
        }

        //检查交易输入是否都是未花费交易输出
        if(!isUnspendTransactionOutput(transaction.getInputs())){
            logger.debug("区块数据异常：交易输入有不是未花费交易输出。");
            return false;
        }

        //校验：是否双花
        if(BlockTool.isDoubleSpendAttackHappen(transaction)){
            logger.debug("区块数据异常，检测到双花攻击。");
            return false;
        }

        //校验哈希作为主键的正确性
        //新产生的Hash不能有重复
        if(!BlockTool.isNewGenerateHashHappenTwiceAndMoreInnerTransaction(transaction)){
            logger.debug("校验数据异常，校验中占用的部分主键已经被使用了。");
            return false;
        }
        //新产生的Hash不能有已经被使用过的
        if(!isNewGenerateHashNeverHappenInBlockchain(transaction)){
            logger.debug("校验数据异常，校验中占用的部分主键已经被使用了。");
            return false;
        }


        //根据交易类型，做进一步的校验
        if(transaction.getTransactionType() == TransactionType.COINBASE){
            /**
             * 激励交易输出可以为空，这时代表矿工放弃了奖励、或者依据规则挖矿激励就是零奖励。
             */
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                logger.debug("交易校验失败：激励交易不能有交易输入。");
                return false;
            }
            if(!BlockTool.isBlockWriteMineAwardRight(incentive,block)){
                logger.debug("交易校验失败：挖矿交易的输出金额不正确。");
                return false;
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            /**
             * 普通交易输出可以为空，这时代表用户将自己的币扔进了黑洞，强制销毁了。
             */
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs == null || inputs.size()==0){
                logger.debug("交易校验失败：普通交易必须有交易输入。");
                return false;
            }
            BigDecimal inputsValue = TransactionTool.getInputsValue(transaction);
            BigDecimal outputsValue = TransactionTool.getOutputsValue(transaction);
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
                if(!TransactionTool.verifyScript(transaction)) {
                    logger.debug("交易校验失败：校验交易签名失败。不合法的交易。");
                    return false;
                }
            }catch (Exception e){
                logger.debug("交易校验失败：校验交易签名失败。不合法的交易。",e);
                return false;
            }
            return true;
        } else {
            logger.debug("区块数据异常，不能识别的交易类型。");
            return false;
        }
    }
    //endregion



    //region 普通查询
    @Override
    public BigInteger queryBlockChainHeight() {
        byte[] bytesBlockChainHeight = LevelDBUtil.get(blockChainDB, KeyHelper.buildBlockChainHeightKey());
        if(bytesBlockChainHeight == null){
            //区块链没有区块高度默认为0
            return BigInteger.valueOf(0);
        }
        return ByteUtil.bytesToBigInteger(bytesBlockChainHeight);
    }

    @Override
    public BigInteger queryTransactionSize() {
        byte[] byteTotalTransactionQuantity = LevelDBUtil.get(blockChainDB, KeyHelper.buildTotalTransactionQuantityKey());
        if(byteTotalTransactionQuantity == null){
            return BigInteger.ZERO;
        }
        return ByteUtil.bytesToBigInteger(byteTotalTransactionQuantity);
    }

    @Override
    public BigInteger queryBlockHeightByBlockHash(String blockHash) {
        byte[] bytesBlockHashToBlockHeightKey = LevelDBUtil.get(blockChainDB, KeyHelper.buildBlockHashToBlockHeightKey(blockHash));
        if(bytesBlockHashToBlockHeightKey == null){
            return null;
        }
        return ByteUtil.bytesToBigInteger(bytesBlockHashToBlockHeightKey);
    }
    //endregion



    //region 区块查询
    @Override
    public Block queryTailBlock() {
        BigInteger blockChainHeight = queryBlockChainHeight();
        if(BigIntegerUtil.isLessEqualThan(blockChainHeight,BigInteger.valueOf(0))){
            return null;
        }
        return queryBlockByBlockHeight(blockChainHeight);
    }
    //TODO 删除NoTransactionBlock ？ 或是 新增区块头实体
    @Override
    public Block queryTailNoTransactionBlock() {
        BigInteger blockChainHeight = queryBlockChainHeight();
        if(BigIntegerUtil.isLessEqualThan(blockChainHeight,BigInteger.valueOf(0))){
            return null;
        }
        return queryNoTransactionBlockByBlockHeight(blockChainHeight);
    }
    @Override
    public Block queryBlockByBlockHeight(BigInteger blockHeight) {
        byte[] bytesBlock = LevelDBUtil.get(blockChainDB, KeyHelper.buildBlockHeightToBlockKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeUtil.decodeToBlock(bytesBlock);
    }
    @Override
    public Block queryNoTransactionBlockByBlockHeight(BigInteger blockHeight) {
        if(blockHeight == null){
            return null;
        }
        byte[] bytesBlock = LevelDBUtil.get(blockChainDB, KeyHelper.buildBlockHeightToNoTransactionBlockKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeUtil.decodeToBlock(bytesBlock);
    }
    //endregion



    //region 交易查询
    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        byte[] bytesTransaction = LevelDBUtil.get(blockChainDB, KeyHelper.buildTransactionHashToTransactionKey(transactionHash));
        if(bytesTransaction==null){
            return null;
        }
        return EncodeDecodeUtil.decodeToTransaction(bytesTransaction);
    }
    @Override
    public List<Transaction> queryTransactionByTransactionHeight(BigInteger from,BigInteger size) {
        List<Transaction> transactionList = new ArrayList<>();
        for(int i=0;BigIntegerUtil.isLessThan(BigInteger.valueOf(i),size);i++){
            byte[] byteTransaction = LevelDBUtil.get(blockChainDB, KeyHelper.buildTransactionSequenceNumberInBlockChainToTransactionKey(from.add(BigInteger.valueOf(i))));
            if(byteTransaction == null){
                break;
            }
            Transaction transaction = EncodeDecodeUtil.decodeToTransaction(byteTransaction);
            transactionList.add(transaction);
        }
        return transactionList;
    }
    //endregion



    //region 交易输出查询
    @Override
    public TransactionOutput queryUnspendTransactionOutputByTransactionOutputHash(String transactionOutputHash) {
        if(transactionOutputHash==null || "".equals(transactionOutputHash)){
            return null;
        }
        byte[] bytesUtxo = LevelDBUtil.get(blockChainDB, KeyHelper.buildUnspendTransactionOutputHashToUnspendTransactionOutputKey(transactionOutputHash));
        if(bytesUtxo == null){
            return null;
        }
        return EncodeDecodeUtil.decodeToTransactionOutput(bytesUtxo);
    }
    @Override
    public List<TransactionOutput> queryUnspendTransactionOutputListByAddress(String address,long from,long size) {
        List<TransactionOutput> transactionOutputList = new ArrayList<>();
        DBIterator iterator = blockChainDB.iterator();
        byte[] addressToUnspendTransactionOutputListKey = KeyHelper.buildAddressToUnspendTransactionOutputListKey(address);
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
    public List<TransactionOutput> queryTransactionOutputListByAddress(String address,long from,long size) {
        List<TransactionOutput> transactionOutputList = new ArrayList<>();
        DBIterator iterator = blockChainDB.iterator();
        byte[] addressToTransactionOutputListKey = KeyHelper.buildAddressToTransactionOuputListKey(address);
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



    //region 拼装WriteBatch
    /**
     * 将区块信息组装成WriteBatch对象
     */
    private WriteBatch createWriteBatch(Block block, BlockChainActionEnum blockChainActionEnum) {
        fillBlockPropertity(block);
        WriteBatch writeBatch = new WriteBatchImpl();
        fillWriteBatch(writeBatch,block,blockChainActionEnum);
        return writeBatch;
    }
    /**
     * 把区块信息组装进WriteBatch对象
     */
    private void fillWriteBatch(WriteBatch writeBatch, Block block, BlockChainActionEnum blockChainActionEnum) {
        //更新区块数据
        byte[] blockHeightKey = KeyHelper.buildBlockHeightToBlockKey(block.getHeight());
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockHeightKey, EncodeDecodeUtil.encode(block));
        }else{
            writeBatch.delete(blockHeightKey);
        }
        //存储无交易信息的区块
        byte[] blockHeightToNoTransactionBlockKey = KeyHelper.buildBlockHeightToNoTransactionBlockKey(block.getHeight());
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            List<Transaction> transactions = block.getTransactions();
            block.setTransactions(null);
            writeBatch.put(blockHeightToNoTransactionBlockKey, EncodeDecodeUtil.encode(block));
            block.setTransactions(transactions);
        }else{
            writeBatch.delete(blockHeightToNoTransactionBlockKey);
        }
        //更新交易数量
        BigInteger transactionSize = queryTransactionSize();
        byte[] totalTransactionQuantityKey = KeyHelper.buildTotalTransactionQuantityKey();
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(totalTransactionQuantityKey, ByteUtil.bigIntegerToBytes(transactionSize.add(BigInteger.valueOf(block.getTransactions().size()))));
        }else{
            writeBatch.put(totalTransactionQuantityKey, ByteUtil.bigIntegerToBytes(transactionSize.subtract(BigInteger.valueOf(block.getTransactions().size()))));
        }
        //区块Hash到区块高度的映射
        byte[] blockHashBlockHeightKey = KeyHelper.buildBlockHashToBlockHeightKey(block.getHash());
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockHashBlockHeightKey, ByteUtil.bigIntegerToBytes(block.getHeight()));
        }else{
            writeBatch.delete(blockHashBlockHeightKey);
        }
        //更新区块链的高度
        byte[] blockChainHeightKey = KeyHelper.buildBlockChainHeightKey();
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockChainHeightKey,ByteUtil.bigIntegerToBytes(block.getHeight()));
        }else{
            writeBatch.put(blockChainHeightKey,ByteUtil.bigIntegerToBytes(block.getHeight().subtract(BigInteger.valueOf(1))));
        }

        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                byte[] hashKey = KeyHelper.buildHashKey(transaction.getTransactionHash());
                //更新交易数据
                byte[] transactionHashKey = KeyHelper.buildTransactionHashToTransactionKey(transaction.getTransactionHash());
                //更新区块链中的交易序列号数据
                byte[] transactionSequenceNumberInBlockChainToTransactionKey = KeyHelper.buildTransactionSequenceNumberInBlockChainToTransactionKey(transaction.getTransactionSequenceNumberInBlockChain());
                if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                    writeBatch.put(hashKey, hashKey);
                    writeBatch.put(transactionHashKey, EncodeDecodeUtil.encode(transaction));
                    writeBatch.put(transactionSequenceNumberInBlockChainToTransactionKey, EncodeDecodeUtil.encode(transaction));
                } else {
                    writeBatch.delete(hashKey);
                    writeBatch.delete(transactionHashKey);
                    writeBatch.delete(transactionSequenceNumberInBlockChainToTransactionKey);
                }
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs!=null){
                    for(TransactionInput txInput:inputs){
                        //更新UTXO数据
                        TransactionOutput unspendTransactionOutput = txInput.getUnspendTransactionOutput();
                        byte[] unspendTransactionOutputHashToUnspendTransactionOutputKey = KeyHelper.buildUnspendTransactionOutputHashToUnspendTransactionOutputKey(unspendTransactionOutput.getTransactionOutputHash());
                        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                            writeBatch.delete(unspendTransactionOutputHashToUnspendTransactionOutputKey);
                        } else {
                            writeBatch.put(unspendTransactionOutputHashToUnspendTransactionOutputKey, EncodeDecodeUtil.encode(unspendTransactionOutput));
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs!=null){
                    for(TransactionOutput output:outputs){
                        byte[] hashKey2 = KeyHelper.buildHashKey(output.getTransactionOutputHash());
                        //更新所有的交易输出
                        byte[] transactionOutputHashToTransactionOutputKey = KeyHelper.buildTransactionOutputHashToTransactionOutputKey(output.getTransactionOutputHash());
                        //更新UTXO数据
                        byte[] unspendTransactionOutputHashToUnspendTransactionOutputKey = KeyHelper.buildUnspendTransactionOutputHashToUnspendTransactionOutputKey(output.getTransactionOutputHash());
                        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                            writeBatch.put(hashKey2, hashKey2);
                            writeBatch.put(transactionOutputHashToTransactionOutputKey, EncodeDecodeUtil.encode(output));
                            writeBatch.put(unspendTransactionOutputHashToUnspendTransactionOutputKey, EncodeDecodeUtil.encode(output));
                        } else {
                            writeBatch.delete(hashKey2);
                            writeBatch.delete(transactionOutputHashToTransactionOutputKey);
                            writeBatch.delete(unspendTransactionOutputHashToUnspendTransactionOutputKey);
                        }
                    }
                }
            }
        }

        addAboutAddressWriteBatch(writeBatch,block,blockChainActionEnum);
    }
    private void addAboutAddressWriteBatch(WriteBatch writeBatch, Block block, BlockChainActionEnum blockChainActionEnum) {
        for(Transaction transaction : block.getTransactions()){
            if(transaction == null){
                return;
            }
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspendTransactionOutput();
                    byte[] addressToUnspendTransactionOutputListKey = KeyHelper.buildAddressToUnspendTransactionOutputListKey(utxo);
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
                    byte[] addressToTransactionOutputListKey = KeyHelper.buildAddressToTransactionOuputListKey(transactionOutput);
                    byte[] addressToUnspendTransactionOutputListKey = KeyHelper.buildAddressToUnspendTransactionOutputListKey(transactionOutput);
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
    /**
     * 补充区块的属性
     */
    private void fillBlockPropertity(Block block) {
        BigInteger transactionSequenceNumberInBlock = BigInteger.ZERO;
        BigInteger transactionSequenceNumberInBlockChain = queryTransactionSize();
        BigInteger blockHeight = block.getHeight();
        List<Transaction> transactions = block.getTransactions();
        BigInteger transactionQuantity = transactions==null?BigInteger.ZERO:BigInteger.valueOf(transactions.size());
        block.setTransactionQuantity(transactionQuantity);
        block.setStartTransactionSequenceNumberInBlockChain(
                BigIntegerUtil.isEquals(transactionQuantity,BigInteger.ZERO)?
                        BigInteger.ZERO:transactionSequenceNumberInBlockChain.add(BigInteger.ONE));
        block.setEndTransactionSequenceNumberInBlockChain(transactionSequenceNumberInBlockChain.add(transactionQuantity));
        if(transactions != null){
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
    }
    //endregion



    //region 私有方法
    /**
     * 检查交易输入是否都是未花费交易输出
     */
    private boolean isUnspendTransactionOutput(List<TransactionInput> inputs) {
        //校验：交易输入是否是UTXO
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
                String unspendTransactionOutputHash = unspendTransactionOutput.getTransactionOutputHash();
                TransactionOutput transactionOutput = queryUnspendTransactionOutputByTransactionOutputHash(unspendTransactionOutputHash);
                if(transactionOutput == null){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isNewGenerateHashNeverHappenInBlockchain(Block block) {
        String blockHash = block.getHash();
        List<Transaction> blockTransactions = block.getTransactions();
        //校验区块Hash是否已经被使用了
        if(isHashExist(blockHash)){
            logger.debug("区块数据异常，区块Hash已经被使用了。");
            return false;
        }
        //校验每一笔交易新产生的Hash是否正确
        if(blockTransactions != null){
            for(Transaction transaction:blockTransactions){
                if(!isNewGenerateHashNeverHappenInBlockchain(transaction)){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isNewGenerateHashNeverHappenInBlockchain(Transaction transaction) {
        String transactionHash = transaction.getTransactionHash();
        List<TransactionOutput> outputs = transaction.getOutputs();
        //校验交易Hash是否已经被使用了
        if(isHashExist(transactionHash)){
            logger.debug("区块数据异常，区块Hash已经被使用了。");
            return false;
        }
        //交易输出Hash是否已经被使用了
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
     * 哈希是否已经存在于区块链之中？
     */
    private boolean isHashExist(String hash){
        byte[] bytesHash = LevelDBUtil.get(blockChainDB, KeyHelper.buildHashKey(hash));
        return bytesHash != null;
    }
    /**
     * 校验激励
     */
    private boolean isIncentiveRight(Block block) { //TODO
        //奖励交易有且只有一笔，且是区块的第一笔交易
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            logger.debug("区块数据异常，没有检测到挖矿奖励交易。");
            return false;
        }
        for(int i=0; i<transactions.size()-1; i++){
            Transaction tx = transactions.get(i);
            if(tx.getTransactionType() == TransactionType.COINBASE){
                logger.debug("区块数据异常，挖矿奖励应当是区块中最后一笔交易。");
                return false;
            }
        }
        Transaction transaction = transactions.get(transactions.size()-1);
        if(transaction.getTransactionType() != TransactionType.COINBASE){
            logger.debug("区块数据异常，区块中最后一笔交易不是挖矿交易。");
            return false;
        }

        //校验奖励交易有且只能有一笔
        //挖矿交易笔数
        int minerTransactionNumber = 0;
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.COINBASE){
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
            if(tx.getTransactionType() == TransactionType.COINBASE){
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
     * 简单的校验Block的连贯性:从高度、哈希、时间戳三个方面检查
     */
    private boolean isBlockHashBlockHeightBlockTimestampRight(Block block) {
        Block tailBlock = queryNoTransactionBlockByBlockHeight(queryBlockChainHeight());
        if(tailBlock == null){
            //校验区块Hash是否连贯
            if(!GlobalSetting.GenesisBlockConstant.FIRST_BLOCK_PREVIOUS_HASH.equals(block.getPreviousBlockHash())){
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
            if(!tailBlock.getHash().equals(block.getPreviousBlockHash())){
                return false;
            }
            //校验区块高度是否连贯
            if(!BigIntegerUtil.isEquals(tailBlock.getHeight().add(BigInteger.valueOf(1)),block.getHeight())){
                return false;
            }
        }
        return true;
    }
    //endregion
}




class KeyHelper {
    //区块链高度key：它对应的值是区块链的高度
    private final static String BLOCK_CHAIN_HEIGHT_KEY = "A";
    //区块链中总的交易数量
    private final static String TOTAL_TRANSACTION_QUANTITY_KEY = "B";

    //哈希标识：哈希(交易哈希、交易输出哈希)的前缀，这里希望系统中所有使用到的哈希都是不同的
    private final static String HASH_PREFIX_FLAG = "C";

    //区块链中的交易序列号
    private final static String TRANSACTION_SEQUENCE_NUMBER_IN_BLOCKCHAIN_TO_TRANSACTION_PREFIX_FLAG = "D";
    //区块高度标识：存储区块链高度到区块的映射
    private final static String BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG = "E";
    //区块高度标识：存储区块链高度到没有交易信息的区块的映射
    private final static String BLOCK_HEIGHT_TO_NO_TRANSACTION_BLOCK_PREFIX_FLAG = "F";
    //标识：存储区块链Hash到区块高度的映射
    private final static String BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG = "G";
    //交易标识：存储交易哈希到交易的映射
    private final static String TRANSACTION_HASH_TO_TRANSACTION_PREFIX_FLAG = "H";
    //交易输出标识：存储交易输出哈希到交易输出的映射
    private final static String TRANSACTION_OUTPUT_HASH_TO_TRANSACTION_OUTPUT_PREFIX_FLAG = "I";
    //未花费的交易输出标识：存储未花费交易输出哈希到未花费交易输出的映射
    private final static String UNSPEND_TRANSACTION_OUTPUT_HASH_TO_UNSPEND_TRANSACTION_OUTPUT_PREFIX_FLAG = "J";
    //地址标识：存储地址到交易输出的映射
    private final static String ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "K";
    //地址标识：存储地址到未花费交易输出的映射
    private final static String ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "L";

    //钱包地址截止标记
    private final static String ADDRESS_END_FLAG = "#" ;




    //拼装数据库Key的值
    public static byte[] buildBlockChainHeightKey() {
        String stringKey = BLOCK_CHAIN_HEIGHT_KEY;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildHashKey(String hash) {
        String stringKey = HASH_PREFIX_FLAG + hash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockHeightToBlockKey(BigInteger blockHeight) {
        String stringKey = BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG + blockHeight;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockHeightToNoTransactionBlockKey(BigInteger blockHeight) {
        String stringKey = BLOCK_HEIGHT_TO_NO_TRANSACTION_BLOCK_PREFIX_FLAG + blockHeight;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockHashToBlockHeightKey(String blockHash) {
        String stringKey = BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG + blockHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionHashToTransactionKey(String transactionHash) {
        String stringKey = TRANSACTION_HASH_TO_TRANSACTION_PREFIX_FLAG + transactionHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionOutputHashToTransactionOutputKey(String transactionOutputHash) {
        String stringKey = TRANSACTION_OUTPUT_HASH_TO_TRANSACTION_OUTPUT_PREFIX_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildUnspendTransactionOutputHashToUnspendTransactionOutputKey(String transactionOutputHash) {
        String stringKey = UNSPEND_TRANSACTION_OUTPUT_HASH_TO_UNSPEND_TRANSACTION_OUTPUT_PREFIX_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToTransactionOuputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getAddress();
        String transactionOutputHash = transactionOutput.getTransactionOutputHash();
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToTransactionOuputListKey(String address) {
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToUnspendTransactionOutputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getAddress();
        String transactionOutputHash = transactionOutput.getTransactionOutputHash();
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputHash;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToUnspendTransactionOutputListKey(String address) {
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTotalTransactionQuantityKey() {
        String stringKey = TOTAL_TRANSACTION_QUANTITY_KEY ;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionSequenceNumberInBlockChainToTransactionKey(BigInteger transactionSequenceNumberInBlockChain) {
        String stringKey = TRANSACTION_SEQUENCE_NUMBER_IN_BLOCKCHAIN_TO_TRANSACTION_PREFIX_FLAG + transactionSequenceNumberInBlockChain;
        return LevelDBUtil.stringToBytes(stringKey);
    }
}