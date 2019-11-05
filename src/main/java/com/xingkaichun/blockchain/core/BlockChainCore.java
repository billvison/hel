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
    //区块校验者
    private Checker checker;
    //矿工
    private Miner miner;

    //区块标识
    private final static String BLOCK_HEIGHT_FLAG = "B_H_F_";
    //交易标识
    private final static String TRANSACTION_UUID_FLAG = "T_U_F_";
    //UTXO标识
    private final static String UNSPEND_TRANSACTION_OUPUT_UUID_FLAG = "U_T_O_U_F";
    //交易输出标识
    private final static String TRANSACTION_OUTPUT_UUID_FLAG = "T_O_U_F";

    //区块链上有可能的最后一个区块【不保证一定是最后的区块】
    private Block possibleLastBlock;

    //监听区块链区块的增删
    private List<BlockChainActionListener> blockChainActionListenerList = new ArrayList<>();

    //保证对区块链增区块、删区块、查区块的操作是同步的。
    private Lock lock = new ReentrantLock();
    //endregion

    //region 构造函数
    /**
     * 构造函数
     * @param dbPath 数据库地址
     * @param checker 校验者
     */
    public BlockChainCore(String dbPath, Checker checker, Miner miner) throws Exception {
        this.blockChainDB = LevelDBUtil.createDB(new File(dbPath,"blockChainDB"));
        this.checker = checker;
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
    public boolean addBlock(Block block) throws Exception {
        if(block==null){
            System.out.println("区块链上新增的区块不能为空。请检测区块。");
            return false;
        }
        lock.lock();
        try{
            //区块数据的校验
            if(!checker.isBlockApplyToBlockChain(this, block)){
                System.out.println("区块链上新增的区块数据不合法。请检测区块。");
                return false;
            }
            WriteBatch writeBatch = createWriteBatch(block,BlockChainActionEnum.ADD_BLOCK);
            LevelDBUtil.put(blockChainDB,writeBatch);

            notifyBlockChainActionListener(createBlockChainActionDataList(block, BlockChainActionEnum.ADD_BLOCK));
            return true;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 删除区块链的尾巴[最后一个]区块
     */
    public Block removeTailBlock() throws Exception {
        lock.lock();
        try{
            Block tailBlock = findLastBlockFromBlock();
            if(tailBlock == null){
                return null;
            }
            WriteBatch writeBatch = createWriteBatch(tailBlock,BlockChainActionEnum.DELETE_BLOCK);
            LevelDBUtil.put(blockChainDB,writeBatch);
            notifyBlockChainActionListener(createBlockChainActionDataList(tailBlock,BlockChainActionEnum.DELETE_BLOCK));
            return tailBlock;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 回滚到老的区块，并新增区块
     */
    public boolean replaceBlocks(List<Block> addBlockList) throws Exception {
        lock.lock();
        try{
            //区块数据的校验
            if(!checker.isBlockListApplyToBlockChain(this, addBlockList)){
                System.out.println("区块链上新增的区块数据不合法。请检测区块。");
                return false;
            }

            //用于记录数据库操作
            WriteBatch writeBatch = new WriteBatchImpl();
            //区块链上将被删掉的区块
            List<Block> deleteBlockList = new ArrayList<>();
            //新增到区块链上第一个区块的高度
            int addedFirstBlockHight = addBlockList.get(0).getBlockHeight();
            //区块链上最后一个区块的高度
            int lastBlockHeight = findLastBlockFromBlock().getBlockHeight();
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

            notifyBlockChainActionListener(createBlockChainActionDataList(deleteBlockList,BlockChainActionEnum.DELETE_BLOCK,addBlockList,BlockChainActionEnum.ADD_BLOCK));
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
        if(writeBatch==null){
            throw new BlockChainCoreException("参数writeBatch没有初始化");
        }
        lock.lock();
        try{
            //区块数据
            if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                writeBatch.put(LevelDBUtil.stringToBytes(addBlockHeightPrefix(block.getBlockHeight())), EncodeDecode.encode(block));
            }else{
                writeBatch.delete(LevelDBUtil.stringToBytes(addBlockHeightPrefix(block.getBlockHeight())));
            }

            //UTXO信息
            List<Transaction> packingTransactionList = block.getTransactions();
            if(packingTransactionList!=null){
                for(Transaction transaction:packingTransactionList){
                    //交易数据
                    if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                        writeBatch.put(LevelDBUtil.stringToBytes(addTransactionUuidPrefix(transaction.getTransactionUUID())), EncodeDecode.encode(transaction));
                    } else {
                        writeBatch.delete(LevelDBUtil.stringToBytes(addTransactionUuidPrefix(transaction.getTransactionUUID())));
                    }
                    ArrayList<TransactionInput> inputs = transaction.getInputs();
                    if(inputs!=null){
                        for(TransactionInput txInput:inputs){
                            if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                                //删除用掉的UTXO
                                writeBatch.delete(LevelDBUtil.stringToBytes(addUnspendTransactionOutputUuidPrefix(txInput.getUtxo().getTransactionOutputUUID())));
                            } else {
                                writeBatch.put(LevelDBUtil.stringToBytes(addUnspendTransactionOutputUuidPrefix(txInput.getUtxo().getTransactionOutputUUID())),EncodeDecode.encode(txInput.getUtxo()));
                            }
                        }
                    }
                    ArrayList<TransactionOutput> outputs = transaction.getOutputs();
                    if(outputs!=null){
                        for(TransactionOutput output:outputs){
                            if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
                                //新产生的UTXO
                                writeBatch.put(LevelDBUtil.stringToBytes(addUnspendTransactionOutputUuidPrefix(output.getTransactionOutputUUID())), EncodeDecode.encode(output));
                                //所有的TXO数据
                                writeBatch.put(LevelDBUtil.stringToBytes(addTransactionOutputPrefix(output.getTransactionOutputUUID())), EncodeDecode.encode(output));
                            } else {
                                writeBatch.delete(LevelDBUtil.stringToBytes(addUnspendTransactionOutputUuidPrefix(output.getTransactionOutputUUID())));
                                writeBatch.delete(LevelDBUtil.stringToBytes(addTransactionOutputPrefix(output.getTransactionOutputUUID())));
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

    //region 对外提供的方法
    /**
     * 查找最后一个区块
     */
    public Block findLastBlockFromBlock() throws Exception {
        lock.lock();
        try{
            //校验区块是否真实存在
            if(possibleLastBlock != null){
                //校验区块是否存在，由于区块链上有删区块的操作，万一代码写了bug，有可能导致possibleLastBlock变量的值是错误的。
                //假设区块不存在，再次检测后，possibleLastBlock将置为null
                possibleLastBlock = findBlockByBlockHeight(possibleLastBlock.getBlockHeight());
            }
            if(possibleLastBlock ==null){
                possibleLastBlock = findBlockByBlockHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
                if(possibleLastBlock == null){
                    return null;
                }
            }
            for(int blockHeight=possibleLastBlock.getBlockHeight()+1;;blockHeight++){
                Block currentBlock = findBlockByBlockHeight(blockHeight);
                if(currentBlock == null){
                    break;
                }else {
                    possibleLastBlock = currentBlock;
                }
            }
            return possibleLastBlock;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 交易输出ID是UTXO吗？
     * @param transactionOutputId 交易输出ID
     */
    public boolean isUTXO(String transactionOutputId) throws Exception {
        lock.lock();
        try{
            TransactionOutput transactionOutput = findUtxoByUtxoUUId(transactionOutputId);
            return transactionOutput!=null;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 查找UTXO
     * @param transactionOutputUUID 交易输出ID
     */
    public TransactionOutput findUtxoByUtxoUUId(String transactionOutputUUID) throws Exception {
        lock.lock();
        try{
            if(transactionOutputUUID==null||"".equals(transactionOutputUUID)){
                return null;
            }
            byte[] utxo = LevelDBUtil.get(blockChainDB, addUnspendTransactionOutputUuidPrefix(transactionOutputUUID));
            if(utxo == null){
                return null;
            }
            return EncodeDecode.decodeToTransactionOutput(utxo);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 查找区块
     * @param blockHeight 区块高度
     */
    public Block findBlockByBlockHeight(int blockHeight) throws Exception {
        lock.lock();
        try{
            byte[] byteBlock = LevelDBUtil.get(blockChainDB,addBlockHeightPrefix(blockHeight));
            if(byteBlock==null){
                return null;
            }
            return EncodeDecode.decodeToBlock(byteBlock);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 查找交易
     * @param transactionUUID 交易ID
     */
    public Transaction findTransactionByUUID(String transactionUUID) throws Exception {
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
    //endregion

    //region 私有方法
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


    public Miner getMiner() {
        return miner;
    }
}
