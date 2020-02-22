package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.Consensus;
import com.xingkaichun.blockchain.core.Incentive;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.BlockUtils;
import com.xingkaichun.blockchain.core.utils.atomic.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
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
 */
public class BlockChainDataBaseDefaultImpl extends BlockChainDataBase {

    private Logger logger = LoggerFactory.getLogger(BlockChainDataBaseDefaultImpl.class);

    //region 变量
    //区块链数据库
    private DB blockChainDB;

    //区块链高度key
    private final static String BLOCK_CHAIN_HEIGHT_KEY = "B_C_H_K";
    //区块标识
    private final static String BLOCK_HEIGHT_PREFIX_FLAG = "B_H_P_F_";
    //交易标识
    private final static String TRANSACTION_UUID_PREFIX_FLAG = "T_U_P_F_";
    //交易输出标识
    private final static String TRANSACTION_OUTPUT_UUID_PREFIX_FLAG = "T_O_U_P_F_";
    //未花费的交易输出标识
    private final static String UNSPEND_TRANSACTION_OUPUT_UUID_PREFIX_FLAG = "U_T_O_U_P_F_";
    //UUID标识
    private final static String UUID_PREFIX_FLAG = "U_F_";
    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    //endregion

    //region 构造函数
    /**
     * 构造函数
     * @param blockChainDataBasePath 区块链数据库地址
     */
    public BlockChainDataBaseDefaultImpl(String blockChainDataBasePath,Incentive incentive,Consensus consensus) throws Exception {
        this.blockChainDB = LevelDBUtil.createDB(new File(blockChainDataBasePath));
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
    //endregion


    //region 区块链提供的通用方法
    @Override
    public Block findTailBlock() throws Exception {
        Integer intBlockChainHeight = obtainBlockChainLength();
        if(intBlockChainHeight == null){
            return null;
        }
        return findBlockByBlockHeight(intBlockChainHeight);
    }

    @Override
    public int obtainBlockChainLength() throws Exception {
        byte[] bytesBlockChainHeight = buildBlockChainHeightKey();
        if(bytesBlockChainHeight == null){
            return 0;
        }
        String strBlockChainHeight = LevelDBUtil.bytesToString(bytesBlockChainHeight);
        Integer intBlockChainHeight = Integer.valueOf(strBlockChainHeight);
        return intBlockChainHeight;
    }

    @Override
    public TransactionOutput findUtxoByUtxoUuid(String transactionOutputUUID) throws Exception {
        if(transactionOutputUUID==null||"".equals(transactionOutputUUID)){
            return null;
        }
        byte[] bytesUtxo = LevelDBUtil.get(blockChainDB, buildUnspendTransactionOutputUuidKey(transactionOutputUUID));
        if(bytesUtxo == null){
            return null;
        }
        return EncodeDecode.decodeToTransactionOutput(bytesUtxo);
    }

    @Override
    public Block findBlockByBlockHeight(int blockHeight) throws Exception {
        byte[] bytesBlock = LevelDBUtil.get(blockChainDB, buildBlockHeightKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecode.decodeToBlock(bytesBlock);
    }

    @Override
    public Transaction findTransactionByTransactionUuid(String transactionUUID) throws Exception {
        byte[] bytesTransaction = LevelDBUtil.get(blockChainDB, buildTransactionUuidKey(transactionUUID));
        if(bytesTransaction==null){
            return null;
        }
        return EncodeDecode.decodeToTransaction(bytesTransaction);
    }

    /**
     * UUID是否已经存在于区块链之中？
     * @param uuid uuid
     */
    public boolean isUuidExist(String uuid){
        byte[] bytesUuid = LevelDBUtil.get(blockChainDB, buildUuidKey(uuid));
        return bytesUuid != null;
    }
    //endregion

    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    public boolean isBlockCanApplyToBlockChain(Block block) throws Exception {
        if(block == null){
            throw new BlockChainCoreException("区块校验失败：区块不能为null。");
        }
        //校验区块的连贯性
        Block tailBlock = findTailBlock();
        if(tailBlock == null){
            //校验时间
            if(block.getTimestamp() <= System.currentTimeMillis()){
                return false;
            }
            //校验区块Previous Hash
            if(!BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH.equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度
            if(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT != block.getHeight()){
                return false;
            }
        } else {
            //校验时间
            if(block.getTimestamp() <= tailBlock.getTimestamp() || block.getTimestamp() > System.currentTimeMillis()){
                return false;
            }
            //校验区块Hash是否连贯
            if(!tailBlock.getHash().equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度是否连贯
            if((tailBlock.getHeight()+1) != block.getHeight()){
                return false;
            }
        }

        //校验写入的MerkleRoot是否与计算得来的一致
        if(!BlockUtils.isBlockWriteMerkleRootRight(block)){
            return false;
        }
        //校验写入的Hash是否与计算得来的一致
        if(!BlockUtils.isBlockWriteHashRight(block)){
            return false;
        }

        //校验共识
        boolean isReachConsensus = consensus.isReachConsensus(this,block);
        if(!isReachConsensus){
            return false;
        }

        //校验奖励交易有且只能有一笔
        //挖矿交易笔数
        int minerTransactionNumber = 0;
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER){
                minerTransactionNumber++;
            }
        }
        if(minerTransactionNumber == 0){
            logger.error("区块数据异常，没有检测到挖矿奖励交易。");
            return false;
        }
        if(minerTransactionNumber>1){
            logger.error("区块数据异常，一个区块只能有一笔挖矿奖励。");
            return false;
        }

        //在不同的交易中，UUID(交易的UUID、交易输入UUID、交易输出UUID)不应该被使用两次或是两次以上
        Set<String> uuidSet = new HashSet<>();
        for(Transaction transaction : block.getTransactions()){
            String transactionUUID = transaction.getTransactionUUID();
            if(!saveUuid(uuidSet,transactionUUID)){
                return false;
            }
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
                String unspendTransactionOutputUUID = unspendTransactionOutput.getTransactionOutputUUID();
                if(!saveUuid(uuidSet,unspendTransactionOutputUUID)){
                    return false;
                }
            }
            ArrayList<TransactionOutput> outputs = transaction.getOutputs();
            for(TransactionOutput transactionOutput : outputs) {
                String transactionOutputUUID = transactionOutput.getTransactionOutputUUID();
                if(!saveUuid(uuidSet,transactionOutputUUID)){
                    return false;
                }
            }
        }

        //从交易角度校验每一笔交易
        for(Transaction tx : block.getTransactions()){
            boolean transactionCanAddToNextBlock = isTransactionCanAddToNextBlock(block,tx);
            if(!transactionCanAddToNextBlock){
                logger.error("区块数据异常，交易异常。");
                return false;
            }
        }
        return true;
    }

    private boolean saveUuid(Set<String> uuidSet, String uuid) {
        if(!UuidUtil.isUuidFormatRight(uuid)){
            return false;
        }
        if(uuidSet.contains(uuid)){
            return false;
        } else {
            uuidSet.add(uuid);
        }
        return true;
    }

    public boolean isTransactionCanAddToNextBlock(Block block, Transaction transaction) throws Exception{
        if(block != null && block.getTimestamp() <= transaction.getTimestamp()){
            logger.error("交易校验失败：挖矿的时间应当在交易的时间之后。");
            return false;
        }

        //校验：只从交易对象层面校验，交易中使用的UUID是否有重复
        Set<String> uuidSet = new HashSet<>();
        String transactionUUID = transaction.getTransactionUUID();
        if(!saveUuid(uuidSet,transactionUUID)){
            return false;
        }
        ArrayList<TransactionInput> inputs = transaction.getInputs();
        for(TransactionInput transactionInput : inputs) {
            TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
            String unspendTransactionOutputUUID = unspendTransactionOutput.getTransactionOutputUUID();
            if(!saveUuid(uuidSet,unspendTransactionOutputUUID)){
                return false;
            }
        }
        ArrayList<TransactionOutput> outputs = transaction.getOutputs();
        for(TransactionOutput transactionOutput : outputs) {
            String transactionOutputUUID = transactionOutput.getTransactionOutputUUID();
            if(!saveUuid(uuidSet,transactionOutputUUID)){
                return false;
            }
        }
        //校验：交易输入UTXO的UUID存在于区块链
        for(TransactionInput transactionInput : inputs) {
            TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
            String unspendTransactionOutputUUID = unspendTransactionOutput.getTransactionOutputUUID();
            TransactionOutput tx = findUtxoByUtxoUuid(unspendTransactionOutputUUID);
            if(tx == null){
                return false;
            }
        }
        //校验：交易UUID和交易输出的UUID不能已经被区块链占用
        if(isUuidExist(transactionUUID)){
            return false;
        }
        for(TransactionOutput transactionOutput : outputs) {
            String transactionOutputUUID = transactionOutput.getTransactionOutputUUID();
            if(isUuidExist(transactionOutputUUID)){
                return false;
            }
        }

        if(transaction.getTransactionType() == TransactionType.MINER){
            if(inputs!=null && inputs.size()!=0){
                logger.error("区块数据异常：挖矿交易的输入只能为空。");
                return false;
            }
            if(outputs == null){
                logger.error("区块数据异常：挖矿交易的输出不能为空。");
                return false;
            }
            if(outputs.size() != 1){
                logger.error("区块数据异常：挖矿交易的输出有且只能有一笔。");
                return false;
            }
            if(!isBlockWriteMineAwardRight(block)){
                logger.error("交易校验失败：挖矿交易的输出金额不正确。不合法的交易。");
                return false;
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            if(inputs==null || inputs.size()==0){
                logger.error("交易校验失败：交易的输入不能为空。不合法的交易。");
                return false;
            }
            for(TransactionOutput o : outputs) {
                if(o.getValue().compareTo(new BigDecimal("0"))<=0){
                    logger.error("交易校验失败：交易的输出<=0。不合法的交易。");
                    return false;
                }
            }
            BigDecimal inputsValue = TransactionUtil.getInputsValue(transaction);
            BigDecimal outputsValue = TransactionUtil.getOutputsValue(transaction);
            if(inputsValue.compareTo(outputsValue) < 0) {
                logger.error("交易校验失败：交易的输入少于交易的输出。不合法的交易。");
                return false;
            }
            //校验 付款方是同一个用户[公钥] 用户花的钱是自己的钱
            if(!TransactionUtil.isSpendOwnUtxo(transaction)){
                logger.error("交易校验失败：交易的付款方有多个。不合法的交易。");
                return false;
            }
            //校验签名验证
            try{
                if(!TransactionUtil.verifySignature(transaction)) {
                    logger.error("交易校验失败：校验交易签名失败。不合法的交易。");
                    return false;
                }
            }catch (InvalidKeySpecException invalidKeySpecException){
                logger.error("交易校验失败：校验交易签名失败。不合法的交易。");
                return false;
            }catch (Exception e){
                logger.error("交易校验失败：校验交易签名失败。不合法的交易。");
                return false;
            }
            return true;
        } else {
            logger.error("区块数据异常，不能识别的交易类型。");
            return false;
        }
    }

    /**
     * 区块中写入的挖矿奖励是否正确？
     * @param block 被校验挖矿奖励是否正确的区块
     * @return
     */
    public boolean isBlockWriteMineAwardRight(Block block){
        //区块中写入的挖矿奖励
        BigDecimal blockWritedMineAward = obtainBlockWriteMineAward(block);
        //目标挖矿奖励
        BigDecimal targetMineAward = incentive.mineAward(this, block);
        return targetMineAward.compareTo(blockWritedMineAward) >= 0 ;
    }

    /**
     * 获取区块中写入的挖矿奖励金额
     */
    public BigDecimal obtainBlockWriteMineAward(Block block) {
        Transaction tx = obtainBlockWriteMineAwardTransaction(block);
        ArrayList<TransactionOutput> outputs = tx.getOutputs();
        BigDecimal value = new BigDecimal("0");
        for(TransactionOutput output:outputs){
            value.add(output.getValue());
        }
        return value;
    }

    /**
     * 获取区块中写入的挖矿奖励交易
     * @param block 区块
     * @return
     */
    public Transaction obtainBlockWriteMineAwardTransaction(Block block) {
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER){
                return tx;
            }
        }
        throw new BlockChainCoreException("区块中没有奖励交易。");
    }


    //region 数据库相关
    //region 拼装数据库Key的值
    private byte[] buildBlockChainHeightKey() {
        String stringKey = BLOCK_CHAIN_HEIGHT_KEY;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildUuidKey(String uuid) {
        String stringKey = UUID_PREFIX_FLAG + uuid;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildBlockHeightKey(int blockHeight) {
        String stringKey = BLOCK_HEIGHT_PREFIX_FLAG + blockHeight;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildTransactionUuidKey(String transactionUUID) {
        String stringKey = TRANSACTION_UUID_PREFIX_FLAG + transactionUUID;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildTransactionOutputUuidKey(String transactionOutputUUID) {
        String stringKey = TRANSACTION_OUTPUT_UUID_PREFIX_FLAG + transactionOutputUUID;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    private byte[] buildUnspendTransactionOutputUuidKey(String transactionOutputUUID) {
        String stringKey = UNSPEND_TRANSACTION_OUPUT_UUID_PREFIX_FLAG + transactionOutputUUID;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    //endregion

    //region 拼装WriteBatch
    /**
     * 将区块信息组装成WriteBatch对象
     */
    public WriteBatch createWriteBatch(Block block, BlockChainActionEnum blockChainActionEnum) throws Exception {
        WriteBatch writeBatch = new WriteBatchImpl();
        fillWriteBatch(writeBatch,block,blockChainActionEnum);
        return writeBatch;
    }

    /**
     * 把区块信息组装进WriteBatch对象
     */
    public void fillWriteBatch(WriteBatch writeBatch, Block block, BlockChainActionEnum blockChainActionEnum) throws Exception {
        if(writeBatch == null){
            throw new BlockChainCoreException("参数writeBatch没有初始化");
        }
        if(block == null){
            throw new BlockChainCoreException("区块不能为空");
        }
        if(blockChainActionEnum == null){
            throw new BlockChainCoreException("区块链动作不能为空");
        }
        //更新区块数据
        byte[] blockHeightKey = buildBlockHeightKey(block.getHeight());
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockHeightKey, EncodeDecode.encode(block));
        }else{
            writeBatch.delete(blockHeightKey);
        }

        byte[] blockChainHeightKey = buildBlockChainHeightKey();
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockChainHeightKey, LevelDBUtil.stringToBytes(String.valueOf(block.getHeight())));
        }else{
            writeBatch.put(blockChainHeightKey, LevelDBUtil.stringToBytes(String.valueOf(block.getHeight()-1)));
        }

        List<Transaction> packingTransactionList = block.getTransactions();
        if(packingTransactionList!=null){
            for(Transaction transaction:packingTransactionList){
                //UUID数据
                byte[] uuidKey = buildUuidKey(transaction.getTransactionUUID());
                //更新交易数据
                byte[] transactionUuidKey = buildTransactionUuidKey(transaction.getTransactionUUID());
                if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                    writeBatch.put(uuidKey, uuidKey);
                    writeBatch.put(transactionUuidKey, EncodeDecode.encode(transaction));
                } else {
                    writeBatch.delete(uuidKey);
                    writeBatch.delete(transactionUuidKey);
                }
                ArrayList<TransactionInput> inputs = transaction.getInputs();
                if(inputs!=null){
                    for(TransactionInput txInput:inputs){
                        //更新UTXO数据
                        byte[] transactionOutputUuidKey = buildUnspendTransactionOutputUuidKey(txInput.getUnspendTransactionOutput().getTransactionOutputUUID());
                        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                            writeBatch.delete(transactionOutputUuidKey);
                        } else {
                            writeBatch.put(transactionOutputUuidKey,EncodeDecode.encode(txInput.getUnspendTransactionOutput()));
                        }
                    }
                }
                ArrayList<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs!=null){
                    for(TransactionOutput output:outputs){
                        //UUID数据
                        byte[] uuidKey2 = buildUuidKey(output.getTransactionOutputUUID());
                        //更新所有的交易输出
                        byte[] transactionOutputUuidKey = buildTransactionOutputUuidKey(output.getTransactionOutputUUID());
                        //更新UTXO数据
                        byte[] unspendTransactionOutputUuidKey = buildUnspendTransactionOutputUuidKey(output.getTransactionOutputUUID());
                        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                            writeBatch.put(uuidKey2, uuidKey2);
                            writeBatch.put(transactionOutputUuidKey, EncodeDecode.encode(output));
                            writeBatch.put(unspendTransactionOutputUuidKey, EncodeDecode.encode(output));
                        } else {
                            writeBatch.delete(uuidKey2);
                            writeBatch.delete(transactionOutputUuidKey);
                            writeBatch.delete(unspendTransactionOutputUuidKey);
                        }
                    }
                }
            }
        }
    }
    //endregion
    //endregion
}