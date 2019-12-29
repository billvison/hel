package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.blockchain.core.miner.Miner;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 区块链
 */
public class BlockChainCore {

    //region 变量
    //区块链数据库
    private DB blockChainDB;
    //矿工
    private Miner miner;

    //区块标识
    private final static String BLOCK_HEIGHT_FLAG = "B_H_F_";
    //交易标识
    private final static String TRANSACTION_UUID_FLAG = "T_U_F_";
    //交易输出标识
    private final static String TRANSACTION_OUTPUT_UUID_FLAG = "T_O_U_F_";
    //UTXO标识
    private final static String UNSPEND_TRANSACTION_OUPUT_UUID_FLAG = "U_T_O_U_F_";
    //UUID标识
    private final static String UUID_FLAG = "U_F_";

    //监听区块链上区块的增删动作
    private List<BlockChainActionListener> blockChainActionListenerList = new ArrayList<>();

    //锁:保证对区块链增区块、删区块、查区块的操作是同步的。
    private Lock lock = new ReentrantLock();
    //endregion

    //region 构造函数
    /**
     * 构造函数
     * @param dbPath 区块链数据库地址
     * @param miner 矿工
     */
    public BlockChainCore(String dbPath, Miner miner) throws Exception {
        this.blockChainDB = LevelDBUtil.createDB(new File(dbPath,"BlockChainDB"));
        this.miner = miner;

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
    /**
     * 区块链新增区块
     */
    public boolean addBlock(Block block, boolean checkBlock, boolean notifyBlockChainActionListener) throws Exception {
        lock.lock();
        try{
            if(checkBlock){
                //区块数据的校验
                if(!miner.isBlockApplyToBlockChain(this, block)){
                    System.out.println("区块链上新增的区块数据不合法。请检测区块。");
                    return false;
                }
            }

            WriteBatch writeBatch = createWriteBatch(block,BlockChainActionEnum.ADD_BLOCK);
            LevelDBUtil.put(blockChainDB,writeBatch);

            if(notifyBlockChainActionListener){
                notifyBlockChainActionListener(createBlockChainActionDataList(block, BlockChainActionEnum.ADD_BLOCK));
            }
            return true;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 删除区块链的尾巴[最后一个]区块
     */
    public Block removeTailBlock(boolean notifyBlockChainActionListener) throws Exception {
        lock.lock();
        try{
            Block tailBlock = findTailBlock();
            if(tailBlock == null){
                return null;
            }
            WriteBatch writeBatch = createWriteBatch(tailBlock,BlockChainActionEnum.DELETE_BLOCK);
            LevelDBUtil.put(blockChainDB,writeBatch);
            if(notifyBlockChainActionListener){
                notifyBlockChainActionListener(createBlockChainActionDataList(tailBlock,BlockChainActionEnum.DELETE_BLOCK));
            }
            return tailBlock;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 回滚老的区块，并新增区块
     */
    public boolean replaceBlocks(List<Block> addBlockList, boolean checkBlock, boolean notifyBlockChainActionListener) throws Exception {
        lock.lock();
        try{
            if(checkBlock){
                //区块数据的校验
                if(!miner.isBlockListApplyToBlockChain(this, addBlockList)){
                    System.out.println("区块链上新增的区块数据不合法。请检测区块。");
                    return false;
                }
            }
            //用于记录数据库操作
            WriteBatch writeBatch = new WriteBatchImpl();
            //区块链上将被删掉的区块
            List<Block> deleteBlockList = new ArrayList<>();
            //新增到区块链上第一个区块的高度
            int addedFirstBlockHight = addBlockList.get(0).getBlockHeight();
            //区块链上最后一个区块的高度
            int lastBlockHeight = findTailBlock().getBlockHeight();
            /**
             * 当lastBlockHeight>=addedFirstBlockHight 表示有替换
             * 当lastBlockHeight+1=addedFirstBlockHight 表示区块都是新增
             * 当lastBlockHeight+1<addedFirstBlockHight 表示新增的区块高度有误
             */
            if(lastBlockHeight>=addedFirstBlockHight){
                for(int blockHeight=addedFirstBlockHight;blockHeight<=lastBlockHeight;blockHeight++){
                    Block block = findBlockByBlockHeight(blockHeight);
                    fillWriteBatch(writeBatch,block,BlockChainActionEnum.DELETE_BLOCK);
                    deleteBlockList.add(block);
                }
            }

            //增 替换的区块
            for(Block block:addBlockList){
                fillWriteBatch(writeBatch,block,BlockChainActionEnum.ADD_BLOCK);
            }

            LevelDBUtil.put(blockChainDB,writeBatch);

            if(notifyBlockChainActionListener){
                notifyBlockChainActionListener(createBlockChainActionDataList(deleteBlockList,BlockChainActionEnum.DELETE_BLOCK,addBlockList,BlockChainActionEnum.ADD_BLOCK));
            }
            return true;
        }finally {
            lock.unlock();
        }
    }
    //endregion

    //region 数据库相关
    //region 拼装数据库Key的值
    private String addTransactionOutputPrefix(String transactionOutputUUID) {
        return TRANSACTION_OUTPUT_UUID_FLAG + transactionOutputUUID;
    }
    private String addUnspendTransactionOutputUuidPrefix(String transactionOutputUUID) {
        return UNSPEND_TRANSACTION_OUPUT_UUID_FLAG + transactionOutputUUID;
    }
    private String addTransactionUuidPrefix(String transactionUUID) {
        return TRANSACTION_UUID_FLAG + transactionUUID;
    }
    private String addBlockHeightPrefix(int blockHeight) {
        return BLOCK_HEIGHT_FLAG + blockHeight;
    }
    private String addUuidPrefix(String uuid) {
        return UUID_FLAG + uuid;
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
        if(blockChainActionEnum == null){
            throw new BlockChainCoreException("区块链动作不能为空");
        }
        if(writeBatch == null){
            throw new BlockChainCoreException("参数writeBatch没有初始化");
        }
        lock.lock();
        try{
            //更新区块数据
            byte[] blockHeightKey = LevelDBUtil.stringToBytes(addBlockHeightPrefix(block.getBlockHeight()));
            if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                writeBatch.put(blockHeightKey, EncodeDecode.encode(block));
            }else{
                writeBatch.delete(blockHeightKey);
            }

            List<Transaction> packingTransactionList = block.getTransactions();
            if(packingTransactionList!=null){
                for(Transaction transaction:packingTransactionList){
                    //UUID数据
                    byte[] uuidKey = LevelDBUtil.stringToBytes(addUuidPrefix(transaction.getTransactionUUID()));
                    //更新交易数据
                    byte[] transactionUuidKey = LevelDBUtil.stringToBytes(addTransactionUuidPrefix(transaction.getTransactionUUID()));
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
                            byte[] transactionOutputUuidKey = LevelDBUtil.stringToBytes(addUnspendTransactionOutputUuidPrefix(txInput.getUnspendTransactionOutput().getTransactionOutputUUID()));
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
                            byte[] uuidKey2 = LevelDBUtil.stringToBytes(addUuidPrefix(output.getTransactionOutputUUID()));
                            //更新所有的交易输出
                            byte[] transactionOutputUuidKey = LevelDBUtil.stringToBytes(addTransactionOutputPrefix(output.getTransactionOutputUUID()));
                            //更新UTXO数据
                            byte[] unspendTransactionOutputUuidKey = LevelDBUtil.stringToBytes(addUnspendTransactionOutputUuidPrefix(output.getTransactionOutputUUID()));
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
        }finally {
            lock.unlock();
        }
    }
    //endregion
    //endregion

    //region 区块链提供的通用方法
    /**
     * 查找区块链上的最后一个区块
     */
    public Block findTailBlock() throws Exception {
        lock.lock();
        try{
            int lastBlockBlockHeight = BlockChainCoreConstants.FIRST_BLOCK_HEIGHT;
            while (true){
                Block currentBlock = findBlockByBlockHeight(lastBlockBlockHeight);
                if(currentBlock == null){
                    //特殊情况:区块链上没有区块
                    if(lastBlockBlockHeight == BlockChainCoreConstants.FIRST_BLOCK_HEIGHT){
                        return null;
                    }
                    break;
                }else {
                    lastBlockBlockHeight = lastBlockBlockHeight<<1;
                }
            }

            int start = lastBlockBlockHeight>>1;
            while (true){
                int middleBlockHeight = (start + lastBlockBlockHeight)/2;
                Block currentBlock = findBlockByBlockHeight(middleBlockHeight);
                Block currentNextBlock = findBlockByBlockHeight(middleBlockHeight+1);
                if(currentBlock!=null && currentNextBlock==null){
                    return currentBlock;
                }else if(currentBlock!=null){
                    start = middleBlockHeight+1;
                }else {
                    lastBlockBlockHeight = middleBlockHeight-1;
                }
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 在区块链中根据 UTXO ID 查找UTXO
     * @param transactionOutputUUID UTXO ID
     */
    public TransactionOutput findUtxoByUtxoUuid(String transactionOutputUUID) throws Exception {
        lock.lock();
        try{
            if(transactionOutputUUID==null||"".equals(transactionOutputUUID)){
                return null;
            }
            byte[] bytesUtxo = LevelDBUtil.get(blockChainDB, addUnspendTransactionOutputUuidPrefix(transactionOutputUUID));
            if(bytesUtxo == null){
                return null;
            }
            return EncodeDecode.decodeToTransactionOutput(bytesUtxo);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 在区块链中根据区块高度查找区块
     * @param blockHeight 区块高度
     */
    public Block findBlockByBlockHeight(int blockHeight) throws Exception {
        lock.lock();
        try{
            byte[] bytesBlock = LevelDBUtil.get(blockChainDB,addBlockHeightPrefix(blockHeight));
            if(bytesBlock==null){
                return null;
            }
            return EncodeDecode.decodeToBlock(bytesBlock);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 在区块链中根据交易ID查找交易
     * @param transactionUUID 交易ID
     */
    public Transaction findTransactionByTransactionUuid(String transactionUUID) throws Exception {
        lock.lock();
        try{
            byte[] byteTransaction = LevelDBUtil.get(blockChainDB, addTransactionUuidPrefix(transactionUUID));
            if(byteTransaction==null){
                return null;
            }
            return EncodeDecode.decodeToTransaction(byteTransaction);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 交易是否已经存在于区块链之中？
     * @param transactionUUID 交易ID
     */
    public boolean isTransactionExist(String transactionUUID) throws Exception {
        lock.lock();
        try{
            Transaction transaction = findTransactionByTransactionUuid(transactionUUID);
            return transaction != null;
        }finally {
            lock.unlock();
        }
    }
    /**
     * UUID是否已经存在于区块链之中？
     * @param uuid uuid
     */
    public boolean isUuidExist(String uuid){
        byte[] bytesUuid = LevelDBUtil.get(blockChainDB,addUuidPrefix(uuid));
        return bytesUuid != null;
    }
    //endregion

    //region 监听器
    public void addBlockChainActionListener(BlockChainActionListener blockChainActionListener){
        lock.lock();
        try{
            blockChainActionListenerList.add(blockChainActionListener);
        }finally {
            lock.unlock();
        }
    }

    private void notifyBlockChainActionListener(List<BlockChainActionData> dataList) {
        lock.lock();
        try{
            for (BlockChainActionListener listener: blockChainActionListenerList) {
                listener.addOrDeleteBlock(dataList);
            }
        }finally {
            lock.unlock();
        }
    }

    private List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) {
        List<BlockChainActionData> dataList = new ArrayList<>();
        BlockChainActionData addData = new BlockChainActionData(block,blockChainActionEnum);
        dataList.add(addData);
        return dataList;
    }
    private List<BlockChainActionData> createBlockChainActionDataList(List<Block> firstBlockList, BlockChainActionEnum firstBlockChainActionEnum, List<Block> nextBlockList, BlockChainActionEnum nextBlockChainActionEnum) {
        List<BlockChainActionData> dataList = new ArrayList<>();
        BlockChainActionData deleteData = new BlockChainActionData(firstBlockList,firstBlockChainActionEnum);
        dataList.add(deleteData);
        BlockChainActionData addData = new BlockChainActionData(nextBlockList,nextBlockChainActionEnum);
        dataList.add(addData);
        return dataList;
    }
    //endregion
}