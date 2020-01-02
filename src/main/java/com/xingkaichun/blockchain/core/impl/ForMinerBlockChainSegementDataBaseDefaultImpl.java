package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.ForMinerBlockChainSegementDataBase;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.BlockChainSegement;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.File;
import java.util.Map;

public class ForMinerBlockChainSegementDataBaseDefaultImpl implements ForMinerBlockChainSegementDataBase {

    //交易池数据库
    private DB forMinerBlockChainSegementDB;

    public ForMinerBlockChainSegementDataBaseDefaultImpl(String dbPath) throws Exception {

        this.forMinerBlockChainSegementDB = LevelDBUtil.createDB(new File(dbPath,"ForMinerBlockChainSegementDataBase"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                forMinerBlockChainSegementDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public boolean addBlockChainSegement(BlockChainSegement blockChainSegement) throws Exception {
        //交易已经持久化进交易池数据库 丢弃交易
        String combineKey = combineKey(blockChainSegement);
        LevelDBUtil.put(forMinerBlockChainSegementDB,combineKey, EncodeDecode.encode(blockChainSegement));
        return true;
    }

    @Override
    public BlockChainSegement getBlockChainSegement() throws Exception {
        BlockChainSegement blockChainSegement = null;
        DBIterator dbIterator = this.forMinerBlockChainSegementDB.iterator();
        while (dbIterator.hasNext()){
            Map.Entry<byte[],byte[]> entry =  dbIterator.next();
            byte[] bytesBlockChainSegement = entry.getValue();
            blockChainSegement = EncodeDecode.decodeToBlockChainSegement(bytesBlockChainSegement);
        }
        return blockChainSegement;
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
