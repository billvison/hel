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
import java.util.concurrent.locks.ReentrantLock;


/**
 * 区块链
 *
 * 注意这是一个线程不安全的实现。在并发的情况下，不保证功能的正确性。
 */
public class BlockChainDataBaseDefaultImpl implements BlockChainDataBase {

    private Logger logger = LoggerFactory.getLogger(BlockChainDataBaseDefaultImpl.class);

    //region 变量
    private Incentive incentive ;
    private Consensus consensus ;

    //区块链数据库
    private DB blockChainDB;

    //区块链高度标识
    private final static String BLOCK_CHAIN_HEIGHT_FLAG = "B_C_H_F_";
    //区块标识
    private final static String BLOCK_HEIGHT_PREFIX_FLAG = "B_H_P_F_";
    //交易标识
    private final static String TRANSACTION_UUID_PREFIX_FLAG = "T_U_P_F_";
    //交易输出标识
    private final static String TRANSACTION_OUTPUT_UUID_PREFIX_FLAG = "T_O_U_P_F_";
    //UTXO标识
    private final static String UNSPEND_TRANSACTION_OUPUT_UUID_PREFIX_FLAG = "U_T_O_U_P_F_";
    //UUID标识
    private final static String UUID_PREFIX_FLAG = "U_F_";
    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private volatile Lock lock = new ReentrantLock();
    //endregion

    //region 构造函数
    /**
     * 构造函数
     * @param dbPath 区块链数据库地址
     */
    public BlockChainDataBaseDefaultImpl(String dbPath,Incentive incentive,Consensus consensus) throws Exception {
        this.blockChainDB = LevelDBUtil.createDB(new File(dbPath,"BlockChainDB"));
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
        lock.lock();
        try{
            WriteBatch writeBatch = createWriteBatch(block,BlockChainActionEnum.ADD_BLOCK);
            LevelDBUtil.write(blockChainDB,writeBatch);
            return true;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public Block removeTailBlock() throws Exception {
        lock.lock();
        try{
            Block tailBlock = findTailBlock();
            if(tailBlock == null){
                return null;
            }
            WriteBatch writeBatch = createWriteBatch(tailBlock,BlockChainActionEnum.DELETE_BLOCK);
            LevelDBUtil.write(blockChainDB,writeBatch);
            return tailBlock;
        }finally {
            lock.unlock();
        }
    }
    //endregion

    //region 数据库相关
    //region 拼装数据库Key的值
    private String addTransactionOutputPrefix(String transactionOutputUUID) {
        return TRANSACTION_OUTPUT_UUID_PREFIX_FLAG + transactionOutputUUID;
    }
    private String addUnspendTransactionOutputUuidPrefix(String transactionOutputUUID) {
        return UNSPEND_TRANSACTION_OUPUT_UUID_PREFIX_FLAG + transactionOutputUUID;
    }
    private String addTransactionUuidPrefix(String transactionUUID) {
        return TRANSACTION_UUID_PREFIX_FLAG + transactionUUID;
    }
    private String addBlockHeightPrefix(int blockHeight) {
        return BLOCK_HEIGHT_PREFIX_FLAG + blockHeight;
    }
    private String addUuidPrefix(String uuid) {
        return UUID_PREFIX_FLAG + uuid;
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
        //更新区块数据
        byte[] blockHeightKey = LevelDBUtil.stringToBytes(addBlockHeightPrefix(block.getHeight()));
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockHeightKey, EncodeDecode.encode(block));
        }else{
            writeBatch.delete(blockHeightKey);
        }

        byte[] blockChainHeightKey = LevelDBUtil.stringToBytes(BLOCK_CHAIN_HEIGHT_FLAG);
        if(BlockChainActionEnum.ADD_BLOCK == blockChainActionEnum){
            writeBatch.put(blockChainHeightKey, LevelDBUtil.stringToBytes(String.valueOf(block.getHeight())));
        }else{
            writeBatch.put(blockChainHeightKey, LevelDBUtil.stringToBytes(String.valueOf(block.getHeight()-1)));
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
    }
    //endregion
    //endregion

    //region 区块链提供的通用方法
    @Override
    public Block findTailBlock() throws Exception {
        byte[] bytesBlockChainHeight = LevelDBUtil.get(blockChainDB,BLOCK_CHAIN_HEIGHT_FLAG);
        if(bytesBlockChainHeight == null){
            return null;
        }
        String strBlockChainHeight = LevelDBUtil.bytesToString(bytesBlockChainHeight);
        int intBlockChainHeight = Integer.valueOf(strBlockChainHeight);
        return findBlockByBlockHeight(intBlockChainHeight);
    }

    @Override
    public TransactionOutput findUtxoByUtxoUuid(String transactionOutputUUID) throws Exception {
        if(transactionOutputUUID==null||"".equals(transactionOutputUUID)){
            return null;
        }
        byte[] bytesUtxo = LevelDBUtil.get(blockChainDB, addUnspendTransactionOutputUuidPrefix(transactionOutputUUID));
        if(bytesUtxo == null){
            return null;
        }
        return EncodeDecode.decodeToTransactionOutput(bytesUtxo);
    }

    @Override
    public Block findBlockByBlockHeight(int blockHeight) throws Exception {
        byte[] bytesBlock = LevelDBUtil.get(blockChainDB,addBlockHeightPrefix(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecode.decodeToBlock(bytesBlock);
    }

    @Override
    public Transaction findTransactionByTransactionUuid(String transactionUUID) throws Exception {
        byte[] bytesTransaction = LevelDBUtil.get(blockChainDB, addTransactionUuidPrefix(transactionUUID));
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
        byte[] bytesUuid = LevelDBUtil.get(blockChainDB,addUuidPrefix(uuid));
        return bytesUuid != null;
    }
    //endregion



































    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    public boolean isBlockCanApplyToBlockChain(Block block) throws Exception {
        if(block==null){
            throw new BlockChainCoreException("区块校验失败：区块不能为null。");
        }
        //校验区块的连贯性
        Block tailBlock = findTailBlock();
        if(tailBlock == null){
            //校验区块Previous Hash
            if(!BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH.equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度
            if(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT != block.getHeight()){
                return false;
            }
        } else {
            //校验区块Hash是否连贯
            if(!tailBlock.getHash().equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度是否连贯
            if((tailBlock.getHeight()+1) != block.getHeight()){
                return false;
            }
        }
        //校验挖矿[区块本身的数据]是否正确
        //TODO
        boolean isReachConsensus = consensus.isReachConsensus(this,block);
        if(!isReachConsensus){
            return false;
        }
        //区块角度检测区块的数据的安全性
        //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
        Set<String> transactionOutputUUIDSet = new HashSet<>();
        //校验即将产生UTXO UUID的唯一性
        Set<String> unspendTransactionOutputUUIDSet = new HashSet<>();
        //挖矿交易笔数，一个区块有且只能有一笔挖矿奖励交易
        int minerTransactionNumber = 0;
        for(Transaction tx : block.getTransactions()){
            String transactionUUID = tx.getTransactionUUID();
            //region 校验交易ID的唯一性
            //校验交易ID的格式
            //校验交易ID的唯一性:之前的区块没用过这个UUID
            //校验交易ID的唯一性:本次校验的区块没有使用这个UUID两次或两次以上
            if(!isUuidAvailableThenAddToSetIfSetNotContainUuid(unspendTransactionOutputUUIDSet,transactionUUID)){
                return false;
            }
            //endregion
            if(tx.getTransactionType() == TransactionType.MINER){
                minerTransactionNumber++;
                //有多个挖矿交易
                if(minerTransactionNumber>1){
                    throw new BlockChainCoreException("区块数据异常，一个区块只能有一笔挖矿奖励。");
                }
                ArrayList<TransactionInput> inputs = tx.getInputs();
                if(inputs!=null && inputs.size()!=0){
                    throw new BlockChainCoreException("交易校验失败：挖矿交易的输入只能为空。不合法的交易。");
                }
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                if(outputs == null){
                    throw new BlockChainCoreException("交易校验失败：挖矿交易的输出不能为空。不合法的交易。");
                }
                if(outputs.size() != 1){
                    throw new BlockChainCoreException("交易校验失败：挖矿交易的输出有且只能有一笔。不合法的交易。");
                }
                TransactionOutput transactionOutput = tx.getOutputs().get(0);
                String unspendTransactionOutputUUID = transactionOutput.getTransactionOutputUUID();
                if(!isUuidAvailableThenAddToSetIfSetNotContainUuid(unspendTransactionOutputUUIDSet,unspendTransactionOutputUUID)){
                    return false;
                }
            } else if(tx.getTransactionType() == TransactionType.NORMAL){
                ArrayList<TransactionInput> inputs = tx.getInputs();
                for(TransactionInput input:inputs){
                    String transactionOutputUUID = input.getUnspendTransactionOutput().getTransactionOutputUUID();
                    //同一个UTXO被多次使用
                    if(transactionOutputUUIDSet.contains(transactionOutputUUID)){
                        throw new BlockChainCoreException("区块数据异常，同一个UTXO在一个区块中多次使用。");
                    }
                    transactionOutputUUIDSet.add(transactionOutputUUID);
                }
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                for(TransactionOutput transactionOutput:outputs){
                    String unspendTransactionOutputUUID = transactionOutput.getTransactionOutputUUID();
                    if(!isUuidAvailableThenAddToSetIfSetNotContainUuid(unspendTransactionOutputUUIDSet,unspendTransactionOutputUUID)){
                        return false;
                    }
                }
            } else {
                throw new BlockChainCoreException("区块数据异常，不能识别的交易类型。");
            }
            boolean check = checkUnBlockChainTransaction(block,tx);
            if(!check){
                throw new BlockChainCoreException("区块数据异常，交易异常。");
            }
        }
        if(minerTransactionNumber == 0){
            throw new BlockChainCoreException("区块数据异常，没有检测到挖矿奖励交易。");
        }
        return true;
    }

    /**
     * UUID格式正确，UUID没有在区块链中被使用，uuidSet包含这个uuid，则返回false，否则，将这个uuid放入uuidSet
     * @param uuidSet
     * @param uuid
     * @return
     */
    private boolean isUuidAvailableThenAddToSetIfSetNotContainUuid(Set<String> uuidSet, String uuid) {
        if(!UuidUtil.isUuidFormatRight(uuid)){
            return false;
        }
        if(isUuidExist(uuid)){
//            throw new BlockChainCoreException("区块数据异常，UUID在区块链中已经被使用了。");
            return false;
        }
        if(uuidSet.contains(uuid)){
//            throw new BlockChainCoreException("区块数据异常，即将产生的UTXO UUID在区块中使用了两次或者两次以上。");
            return false;
        } else {
            uuidSet.add(uuid);
        }
        return true;
    }

    /**
     * 校验(未打包进区块链的)交易的合法性
     * 奖励交易校验需要传入block参数
     */
    public boolean checkUnBlockChainTransaction(Block block, Transaction transaction) throws Exception{
        if(transaction.getTransactionType() == TransactionType.MINER){
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            if(inputs!=null && inputs.size()!=0){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输入只能为空。不合法的交易。");
            }
            ArrayList<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出不能为空。不合法的交易。");
            }
            if(outputs.size() != 1){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出有且只能有一笔。不合法的交易。");
            }
            if(!isBlockWriteMineAwardRight(block)){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出金额不正确。不合法的交易。");
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            if(inputs==null || inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输入不能为空。不合法的交易。");
            }
            for(TransactionInput i : inputs) {
                if(i.getUnspendTransactionOutput() == null){
                    throw new BlockChainCoreException("交易校验失败：交易的输入UTXO不能为空。不合法的交易。");
                }
                if(findUtxoByUtxoUuid(i.getUnspendTransactionOutput().getTransactionOutputUUID())!=null){
                    throw new BlockChainCoreException("交易校验失败：交易的输入不是UTXO。不合法的交易。");
                }
            }
            if(inputs==null || inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输出不能为空。不合法的交易。");
            }
            //存放交易用过的UTXO
            Set<String> input_UTXO_Ids = new HashSet<>();
            for(TransactionInput i : inputs) {
                String utxoId = i.getUnspendTransactionOutput().getTransactionOutputUUID();
                //校验 同一张钱不能使用两次
                if(input_UTXO_Ids.contains(utxoId)){
                    throw new BlockChainCoreException("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
                }
                input_UTXO_Ids.add(utxoId);
            }
            ArrayList<TransactionOutput> outputs = transaction.getOutputs();
            for(TransactionOutput o : outputs) {
                if(o.getValue().compareTo(new BigDecimal("0"))<=0){
                    throw new BlockChainCoreException("交易校验失败：交易的输出<=0。不合法的交易。");
                }
            }
            BigDecimal inputsValue = TransactionUtil.getInputsValue(transaction);
            BigDecimal outputsValue = TransactionUtil.getOutputsValue(transaction);
            if(inputsValue.compareTo(outputsValue) < 0) {
                throw new BlockChainCoreException("交易校验失败：交易的输入少于交易的输出。不合法的交易。");
            }
            //校验 付款方是同一个用户[公钥] 用户花的钱是自己的钱
            if(!TransactionUtil.isOnlyOneSender(transaction)){
                throw new BlockChainCoreException("交易校验失败：交易的付款方有多个。不合法的交易。");
            }
            //校验签名验证
            try{
                if(!TransactionUtil.verifySignature(transaction)) {
                    throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
                }
            }catch (InvalidKeySpecException invalidKeySpecException){
                throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
            }catch (Exception e){
                throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
            }
            return true;
        } else {
            throw new BlockChainCoreException("区块数据异常，不能识别的交易类型。");
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
        return targetMineAward.compareTo(blockWritedMineAward) != 0 ;
    }

    /**
     * 获取区块中写入的挖矿奖励金额
     */
    public BigDecimal obtainBlockWriteMineAward(Block block) {
        Transaction tx = obtainBlockWriteMineAwardTransaction(block);
        ArrayList<TransactionOutput> outputs = tx.getOutputs();
        TransactionOutput mineAwardTransactionOutput = outputs.get(0);
        return mineAwardTransactionOutput.getValue();
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


    public Incentive getIncentive() {
        return incentive;
    }

    public void setIncentive(Incentive incentive) {
        this.incentive = incentive;
    }

    public Consensus getConsensus() {
        return consensus;
    }

    public void setConsensus(Consensus consensus) {
        this.consensus = consensus;
    }
}