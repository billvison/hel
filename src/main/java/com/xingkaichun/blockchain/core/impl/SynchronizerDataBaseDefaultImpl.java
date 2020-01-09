package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.SynchronizerDataBase;
import com.xingkaichun.blockchain.core.TransactionDataBase;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.File;
import java.util.Map;

//TODO 尚未实现
public class SynchronizerDataBaseDefaultImpl implements SynchronizerDataBase {

    //交易池数据库
    private DB forMinerBlockChainSegementDB;
    private TransactionDataBase transactionDataBase;

    public SynchronizerDataBaseDefaultImpl(String dbPath, TransactionDataBase transactionDataBase) throws Exception {

        this.forMinerBlockChainSegementDB = LevelDBUtil.createDB(new File(dbPath,"SynchronizerDataBase"));
        this.transactionDataBase = transactionDataBase;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                forMinerBlockChainSegementDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public boolean addBlock(String nodeId, Block block) throws Exception {

        transactionDataBase.insertBlock(block);

        String combineKey = combineKey(block);
        LevelDBUtil.put(forMinerBlockChainSegementDB,combineKey, EncodeDecode.encode(block));
        return true;
    }

    @Override
    public Block getNextBlock(String nodeId) throws Exception {
        DBIterator dbIterator = this.forMinerBlockChainSegementDB.iterator();
        while (dbIterator.hasNext()){
            Map.Entry<byte[],byte[]> entry =  dbIterator.next();
            byte[] bytesBlockChainSegement = entry.getValue();
            return EncodeDecode.decodeToBlock(bytesBlockChainSegement);
        }
        return null;
    }

    @Override
    public void deleteTransferData(String nodeId) throws Exception {
    }

    @Override
    public boolean hasDataTransferFinishFlag(String nodeId) {
        return false;
    }

    @Override
    public String getDataTransferFinishFlagNodeId() throws Exception {
        return null;
    }

    @Override
    public void addDataTransferFinishFlag(String nodeId) throws Exception {

    }

    @Override
    public void clearDataTransferFinishFlag(String nodeId) throws Exception {

    }

    /**
     * key=blockHeight+blockHash
     * @return
     */
    private String combineKey(Block block) {
        String key = block.getHeight()+block.getHash();
        return key;
    }
}
