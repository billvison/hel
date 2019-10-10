package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;
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
    private DB BlockChain_DB;
    //区块校验者
    private Checker checker;

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
    public BlockChainCore(String dbPath, Checker checker) throws Exception {
        this.BlockChain_DB = LevelDBUtil.createDB(new File(dbPath,"BlockChain_DB"));
        this.checker = checker;


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                BlockChain_DB.close();
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
            if(!checker.checkBlockOfNextAddToBlockChain(this, block)){
                System.out.println("区块链上新增的区块数据不合法。请检测区块。");
                return false;
            }
            WriteBatch writeBatch = createWriteBatch(block,true,false);
            LevelDBUtil.put(BlockChain_DB,writeBatch);
            notifyBlockChainActionListener(block,true,false);
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
            WriteBatch writeBatch = createWriteBatch(tailBlock,false,true);
            LevelDBUtil.put(BlockChain_DB,writeBatch);
            notifyBlockChainActionListener(tailBlock,false,true);
            return tailBlock;
        }finally {
            lock.unlock();
        }
    }

    /**
     * //TODO 有BUG
     * 回滚到老的区块，并新增区块
     */
/*
    public boolean backAndAddBlocks(List<Block> addBlockList) throws Exception {
        lock.lock();
        try{
            if(addBlockList==null || addBlockList.size()==0){
                return true;
            }

            Block addedFirstBlock = addBlockList.get(0);
            int addedFirstBlockHight = addedFirstBlock.getBlockHeight();
            Block deleteUntilBlock = findBlockByBlockHeight(addedFirstBlock.getBlockHeight()-1);

            if (deleteUntilBlock == null){
                return false;
            }

            for(int i=0;i<addBlockList.size();i++){
                if(i==0){
                    //校验新增的区块们
                    //TODO 有BUG
                    boolean continueBlock = continueBlock(deleteUntilBlock,addedFirstBlock);
                    if(!continueBlock){
                        return false;
                    }
                }else{
                    boolean continueBlock = continueBlock(addBlockList.get(i-1),addBlockList.get(i));
                    if(!continueBlock){
                        return false;
                    }
                }
            }

            WriteBatch writeBatch = new WriteBatchImpl();

            Block lastBlock = findLastBlockFromBlock();
            int lastBlockHeight = lastBlock.getBlockHeight();

            //删 被替换掉的区块
            List<Block> deleteBlockList = new ArrayList<>();
            for(int blockHeight=addedFirstBlockHight;blockHeight<=lastBlockHeight;blockHeight++){
                Block block = findBlockByBlockHeight(blockHeight);
                fillWriteBatch(writeBatch,block,false,true);
                deleteBlockList.add(block);
            }

            //增 替换的区块
            for(Block block:addBlockList){
                fillWriteBatch(writeBatch,block,true,false);
            }

            LevelDBUtil.put(BlockChain_DB,writeBatch);

            notifyBlockChainActionListener(deleteBlockList,false,true);
            notifyBlockChainActionListener(addBlockList,true,false);
            return true;
        }finally {
            lock.unlock();
        }
    }
*/
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
     * @param block 区块
     * @param addBlock 是新增区块？
     * @param deleteBlock 是删除区块？
     */
    private WriteBatch createWriteBatch(Block block, boolean addBlock, boolean deleteBlock) throws Exception {
        WriteBatch writeBatch = new WriteBatchImpl();
        fillWriteBatch(writeBatch,block,addBlock,deleteBlock);
        return writeBatch;
    }

    /**
     * 把区块信息组装进WriteBatch对象
     * @param block 区块
     * @param addBlock 是新增区块？
     * @param deleteBlock 是删除区块？
     */
    private void fillWriteBatch(WriteBatch writeBatch, Block block, boolean addBlock, boolean deleteBlock) throws Exception {
        if(addBlock == deleteBlock){
            throw new BlockChainCoreException("参数addBlock、deleteBlock互斥，不允许同时为true、或同时为false");
        }
        if(writeBatch==null){
            throw new BlockChainCoreException("参数writeBatch没有初始化");
        }
        lock.lock();
        try{
            //区块数据
            if(addBlock){
                writeBatch.put(addBlockHeightPrefix(block.getBlockHeight()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8), EncodeDecode.encode(block));
            }else{
                writeBatch.delete(addBlockHeightPrefix(block.getBlockHeight()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8));
            }

            //UTXO信息
            List<Transaction> packingTransactionList = block.getTransactions();
            if(packingTransactionList!=null){
                for(Transaction transaction:packingTransactionList){
                    //交易数据
                    if(addBlock){
                        writeBatch.put(addTransactionUuidPrefix(transaction.getTransactionUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8), EncodeDecode.encode(transaction));
                    } else {
                        writeBatch.delete(addTransactionUuidPrefix(transaction.getTransactionUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8));
                    }
                    ArrayList<TransactionInput> inputs = transaction.getInputs();
                    if(inputs!=null){
                        for(TransactionInput txInput:inputs){
                            if(addBlock){
                                //删除用掉的UTXO
                                writeBatch.delete(addUnspendTransactionOutputUuidPrefix(txInput.getUtxo().getTransactionOutputUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8));
                            } else {
                                writeBatch.put(addUnspendTransactionOutputUuidPrefix(txInput.getUtxo().getTransactionOutputUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8),EncodeDecode.encode(txInput.getUtxo()));
                            }
                        }
                    }
                    ArrayList<TransactionOutput> outputs = transaction.getOutputs();
                    if(outputs!=null){
                        for(TransactionOutput output:outputs){
                            if(addBlock){
                                //新产生的UTXO
                                writeBatch.put(addUnspendTransactionOutputUuidPrefix(output.getTransactionOutputUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8), EncodeDecode.encode(output));
                                //所有的TXO数据
                                writeBatch.put(addTransactionOutputPrefix(output.getTransactionOutputUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8), EncodeDecode.encode(output));
                            } else {
                                writeBatch.delete(addUnspendTransactionOutputUuidPrefix(output.getTransactionOutputUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8));
                                writeBatch.delete(addTransactionOutputPrefix(output.getTransactionOutputUUID()).getBytes(BlockChainCoreConstants.CHARSET_UTF_8));
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
            byte[] utxo = LevelDBUtil.get(BlockChain_DB, addUnspendTransactionOutputUuidPrefix(transactionOutputUUID));
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
            byte[] byteBlock = LevelDBUtil.get(BlockChain_DB,addBlockHeightPrefix(blockHeight));
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
            byte[] byteTransaction = LevelDBUtil.get(BlockChain_DB, addTransactionUuidPrefix(transactionUUID));
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
    private void notifyBlockChainActionListener(Block block, boolean addBlock, boolean deleteBlock) {
        lock.lock();
        try{
            for (BlockChainActionListener listener: blockChainActionListenerList) {
                listener.addOrDeleteBlock(block,addBlock,deleteBlock);
            }
        }finally {
            lock.unlock();
        }
    }
    //endregion
}
