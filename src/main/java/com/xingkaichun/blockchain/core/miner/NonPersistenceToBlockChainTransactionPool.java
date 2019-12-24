package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 交易池
 * 所有没有持久化进区块链的交易，都应该放入交易池。
 * 其它对象可以从交易池获取这部分交易数据，然后进行自己的活动。例如矿工可以从交易池获取挖矿的原材料(交易数据)进行挖矿活动。
 */
public class NonPersistenceToBlockChainTransactionPool {

    //交易池数据库
    private DB transactionPoolDB;

    public NonPersistenceToBlockChainTransactionPool(String dbPath) throws Exception {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(dbPath,"TransactionPoolDB"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                transactionPoolDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * 添加交易进交易池
     */
    public boolean addTransaction(Transaction transaction) throws Exception {
        //交易已经持久化进交易池数据库 丢弃交易
        if(isTransactionExsitInPool(transaction.getTransactionUUID())){
            return false;
        }
        synchronized (BlockChainCore.class){
            LevelDBUtil.put(transactionPoolDB,transaction.getTransactionUUID(), EncodeDecode.encode(transaction));
        }
        return true;
    }

    /**
     * 从交易池获取交易
     */
    public List<Transaction> getTransactionList() throws Exception {
        synchronized (BlockChainCore.class){
            List<Transaction> transactionList = new ArrayList<>();
            DBIterator dbIterator = this.transactionPoolDB.iterator();
            while (dbIterator.hasNext()){
                Map.Entry<byte[],byte[]> entry =  dbIterator.next();
                byte[] byteTransaction = entry.getValue();
                Transaction transaction = EncodeDecode.decodeToTransaction(byteTransaction);
                transactionList.add(transaction);
            }
            return transactionList;
        }
    }

    /**
     * 交易是否已经存在于交易池
     * @param transactionUUID 交易ID
     */
    private boolean isTransactionExsitInPool(String transactionUUID) throws Exception {
        byte[] byteTransaction = LevelDBUtil.get(transactionPoolDB,transactionUUID);
        return byteTransaction != null;
    }
}
