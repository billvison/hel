package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 交易池
 * 所有没有打包成区块放入区块链数据库的交易，都由交易池管理。
 */
public class TransactionPool {

    //交易池数据库
    private DB transactionPool_DB;
    private BlockChainCore blockChainCore;

    //等待持久化进数据库的交易
    private List<Transaction> waitForSendToDbTransactionList;




    public TransactionPool(String dbPath, BlockChainCore blockChainCore) throws Exception {


        this.waitForSendToDbTransactionList = new ArrayList<>();

        this.transactionPool_DB = LevelDBUtil.createDB(new File(dbPath,"TransactionPool_DB"));
        this.blockChainCore = blockChainCore;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                transactionPool_DB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        new SendTransactionToDbThread(this.waitForSendToDbTransactionList).start();
    }

    /**
     * 添加交易
     */
    public boolean addTransaction(Transaction transaction) throws Exception {
        //交易校验失败 丢弃交易
        if(!blockChainCore.getMiner().checkUnBlockChainTransaction(blockChainCore,null,transaction)){
            return false;
        }
        //交易已经存在于区块链 丢弃交易
        if(blockChainCore.findTransactionByUUID(transaction.getTransactionUUID())!=null){
            return false;
        }
        //交易已经持久化进交易池数据库 丢弃交易
        if(isExsitTransaction(transaction.getTransactionUUID())){
            return false;
        }
        synchronized (BlockChainCore.class){
            waitForSendToDbTransactionList.add(transaction);
        }
        return true;
    }

    /**
     * 获取挖矿的原料:交易
     */
    public List<Transaction> getTransactionListForMine() throws Exception {
        //TODO 从数据库取数据
        synchronized (BlockChainCore.class){
            List<Transaction> forMineTransactionList = new ArrayList<>();
            //TODO 如何同步了别的节点的区块，若删除了自己的的区块，被删除区块的交易信息应当再次加入到交易池
            DBIterator dbIterator = this.transactionPool_DB.iterator();
            while (dbIterator.hasNext()){
                Map.Entry<byte[],byte[]> entry =  dbIterator.next();
                byte[] byteTransaction = entry.getValue();
                Transaction transaction = EncodeDecode.decodeToTransaction(byteTransaction);
                forMineTransactionList.add(transaction);
            }
            return forMineTransactionList;
        }
    }

    /**
     * 持久化交易数据
     * 原因:不可预知有多少交易
     */
    class SendTransactionToDbThread extends Thread{

        private List<Transaction> transactionList;

        public SendTransactionToDbThread(List<Transaction> transactionList) {
            super("交易数据持久化线程");
            this.transactionList = transactionList;
        }

        @Override
        public void run() {
            while (true){
                WriteBatch writeBatch = new WriteBatchImpl();
                int size = 0;
                try {
                    synchronized (TransactionPool.class){
                        size = transactionList.size();
                        for(Transaction transaction: transactionList){
                            writeBatch.put(LevelDBUtil.stringToBytes(transaction.getTransactionUUID()), EncodeDecode.encode(transaction));
                        }
                        transactionList.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(size>0){
                    LevelDBUtil.put(transactionPool_DB,writeBatch);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isExsitTransaction(String transactionUUID) throws Exception {
        byte[] byteTransaction = LevelDBUtil.get(transactionPool_DB,transactionUUID);
        return byteTransaction != null;
    }
    public Transaction findTransactionById(String transactionUUID) throws Exception {
        byte[] byteTransaction = LevelDBUtil.get(transactionPool_DB,transactionUUID);
        if(byteTransaction==null){
            return null;
        }
        return EncodeDecode.decodeToTransaction(byteTransaction);
    }
}
