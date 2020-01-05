package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.ForMinerTransactionDataBase;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForMinerTransactionDataBaseDefaultImpl implements ForMinerTransactionDataBase {

    private DB transactionPoolDB;

    public ForMinerTransactionDataBaseDefaultImpl(String dbPath) throws Exception {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(dbPath,"ForMinerTransactionDataBase"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                transactionPoolDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void insertTransaction(Transaction transaction) throws Exception {

        //校验签名 防止签名错误的交易加入交易池
        boolean verifySignature = TransactionUtil.verifySignature(transaction);
        if(!verifySignature){
            throw new BlockChainCoreException("新增交易失败，交易签名错误。");
        }

        //交易已经持久化进交易池数据库 丢弃交易
        String combineKey = combineKey(transaction);
        synchronized (BlockChainDataBase.class){
            LevelDBUtil.put(transactionPoolDB,combineKey, EncodeDecode.encode(transaction));
        }
    }

    @Override
    public void insertTransactionList(List<Transaction> transactionList) throws Exception {
        for(Transaction transaction:transactionList){
            boolean verifySignature = TransactionUtil.verifySignature(transaction);
            if(!verifySignature){
                throw new BlockChainCoreException("新增交易失败，交易签名错误。");
            }
        }
        WriteBatch writeBatch = new WriteBatchImpl();
        for(Transaction transaction:transactionList){
            String combineKey = combineKey(transaction);
            writeBatch.put(LevelDBUtil.stringToBytes(combineKey),EncodeDecode.encode(transaction));
        }
        synchronized (BlockChainDataBase.class){
            LevelDBUtil.put(transactionPoolDB, writeBatch);
        }
    }

    public List<Transaction> selectTransactionList(int from, int size) throws Exception {
        synchronized (BlockChainDataBase.class){
            List<Transaction> transactionList = new ArrayList<>();
            DBIterator dbIterator = this.transactionPoolDB.iterator();
            int index = 0;
            while (dbIterator.hasNext()){
                if(index>=from && from<from+size){
                    Map.Entry<byte[],byte[]> entry =  dbIterator.next();
                    byte[] byteTransaction = entry.getValue();
                    Transaction transaction = EncodeDecode.decodeToTransaction(byteTransaction);
                    transactionList.add(transaction);
                } else {
                    break;
                }
                index++;
            }
            return transactionList;
        }
    }

    @Override
    public void deleteTransaction(Transaction transaction) throws Exception {
        String combineKey = combineKey(transaction);
        LevelDBUtil.delete(transactionPoolDB,combineKey);
    }

    @Override
    public void deleteTransactionList(List<Transaction> transactionList) throws Exception {
        if(transactionList == null){
            return;
        }
        for(Transaction transaction:transactionList){
            deleteTransaction(transaction);
        }
    }

    /**
     * 生成一个关键字(公钥+交易UUID)
     * 为什么生成这么一个KEY？
     * 假设有客户端恶意生成和别人相同的交易UUID。
     * 因为KEY是有公钥参与生成，客户端不可能冒充别人(想要冒充别人得有别人的公钥与私钥)
     * 一个客户端多次发送同一笔交易，会计算成同一个KEY，所以在交易池只会保存一笔。
     * 因为KEY是有公钥参与生成，所以在交易池看来，它们是不同的交易，它们都可以持久化进交易池。
     * @param transaction 交易
     */
    private String combineKey(Transaction transaction) {
        return TransactionUtil.getSender(transaction).getValue() + transaction.getTransactionUUID();
    }
}
