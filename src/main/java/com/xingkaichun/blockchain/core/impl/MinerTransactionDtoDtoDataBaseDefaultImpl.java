package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.blockchain.core.TransactionDataBase;
import com.xingkaichun.blockchain.core.dto.DtoUtils;
import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinerTransactionDtoDtoDataBaseDefaultImpl extends MinerTransactionDtoDataBase {

    private DB transactionPoolDB;
    private TransactionDataBase transactionDataBase;

    public MinerTransactionDtoDtoDataBaseDefaultImpl(String dbPath, TransactionDataBase transactionDataBase) throws Exception {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(dbPath));
        this.transactionDataBase = transactionDataBase;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                transactionPoolDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void insertTransactionDTO(TransactionDTO transactionDTO) throws Exception {

        //校验签名 防止签名错误的交易加入交易池
        transactionDataBase.insertTransaction(transactionDTO);

        //交易已经持久化进交易池数据库 丢弃交易
        synchronized (BlockChainDataBase.class){
            LevelDBUtil.put(transactionPoolDB,transactionDTO.getTransactionUUID(), encode(transactionDTO));
        }
    }

    @Override
    public void insertTransactionDtoList(List<TransactionDTO> transactionDTOList) throws Exception {
        WriteBatch writeBatch = new WriteBatchImpl();
        for(TransactionDTO transactionDTO:transactionDTOList){
            writeBatch.put(LevelDBUtil.stringToBytes(transactionDTO.getTransactionUUID()),encode(transactionDTO));
        }
        synchronized (BlockChainDataBase.class){
            LevelDBUtil.write(transactionPoolDB, writeBatch);
        }
    }

    @Override
    public List<Transaction> selectTransactionList(BlockChainDataBase blockChainDataBase,int from, int size) throws Exception {
        synchronized (BlockChainDataBase.class){
            List<TransactionDTO> transactionDtoList = new ArrayList<>();
            DBIterator dbIterator = this.transactionPoolDB.iterator();
            int index = 0;
            while (dbIterator.hasNext()){
                if(index>=from && from<from+size){
                    Map.Entry<byte[],byte[]> entry =  dbIterator.next();
                    byte[] byteTransaction = entry.getValue();
                    TransactionDTO transactionDTO = decodeToTransactionDTO(byteTransaction);
                    transactionDtoList.add(transactionDTO);
                } else {
                    break;
                }
                index++;
            }
            List<Transaction> transactionList = classCast(blockChainDataBase,transactionDtoList);
            return transactionList;
        }
    }

    @Override
    public void deleteTransaction(TransactionDTO transactionDTO) throws Exception {
        LevelDBUtil.delete(transactionPoolDB,transactionDTO.getTransactionUUID());
    }

    @Override
    public void deleteTransactionList(List<Transaction> transactionList) throws Exception {
        if(transactionList == null){
            return;
        }
        WriteBatch writeBatch = new WriteBatchImpl();
        for(Transaction transaction:transactionList){
            writeBatch.delete(LevelDBUtil.stringToBytes(transaction.getTransactionUUID()));
        }
        LevelDBUtil.write(transactionPoolDB,writeBatch);
    }

    private static byte[] encode(TransactionDTO transactionDTO) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(transactionDTO);
        byte[] bytesTransactionDTO = byteArrayOutputStream.toByteArray();
        return bytesTransactionDTO;
    }

    private static TransactionDTO decodeToTransactionDTO(byte[] bytesTransactionDTO) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesTransactionDTO);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        TransactionDTO transactionDTO = (TransactionDTO) objectInputStream.readObject();
        return transactionDTO;
    }

    private List<Transaction> classCast(BlockChainDataBase blockChainDataBase, List<TransactionDTO> transactionDtoList) {
        List<Transaction> transactionList = new ArrayList<>();
        if(transactionDtoList != null && transactionDtoList.size()!=0){
            for(TransactionDTO transactionDTO:transactionDtoList){
                try {
                    Transaction transaction = DtoUtils.classCast(blockChainDataBase,transactionDTO);
                    transactionList.add(transaction);
                } catch (Exception e){

                }
            }
        }
        return transactionList;
    }
}
