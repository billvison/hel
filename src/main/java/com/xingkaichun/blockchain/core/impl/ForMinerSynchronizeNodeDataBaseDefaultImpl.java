package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.ForMinerSynchronizeNodeDataBase;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.BlockChainSegement;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.File;
import java.util.Map;

//TODO 尚未实现
public class ForMinerSynchronizeNodeDataBaseDefaultImpl implements ForMinerSynchronizeNodeDataBase {

    //交易池数据库
    private DB forMinerBlockChainSegementDB;

    public ForMinerSynchronizeNodeDataBaseDefaultImpl(String dbPath) throws Exception {

        this.forMinerBlockChainSegementDB = LevelDBUtil.createDB(new File(dbPath,"ForMinerSynchronizeNodeDataBase"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                forMinerBlockChainSegementDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public boolean addBlockChainSegement(String nodeId, BlockChainSegement blockChainSegement) throws Exception {
        //交易已经持久化进交易池数据库 丢弃交易
        String combineKey = combineKey(blockChainSegement);
        LevelDBUtil.put(forMinerBlockChainSegementDB,combineKey, EncodeDecode.encode(blockChainSegement));
        return true;
    }

    @Override
    public BlockChainSegement getNextBlockChainSegement(String nodeId) throws Exception {
        BlockChainSegement blockChainSegement = null;
        DBIterator dbIterator = this.forMinerBlockChainSegementDB.iterator();
        while (dbIterator.hasNext()){
            Map.Entry<byte[],byte[]> entry =  dbIterator.next();
            byte[] bytesBlockChainSegement = entry.getValue();
            blockChainSegement = EncodeDecode.decodeToBlockChainSegement(bytesBlockChainSegement);
        }
        return blockChainSegement;
    }

    @Override
    public void deleteTransferData(String nodeId) throws Exception {
        // TODO
        // LevelDBUtil.deleteTransferData(forMinerBlockChainSegementDB,combineKey(blockChainSegement));
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
     * @param blockChainSegement
     * @return
     */
    private String combineKey(BlockChainSegement blockChainSegement) {
        String key = "";
        for(Block block:blockChainSegement.getBlockList()){
            key = key+block.getHeight()+block.getHash();
        }
        return key;
    }
}
