package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.helloworldblockchain.core.TransactionDataBase;
import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinerTransactionDtoDtoDataBaseDefaultImpl extends MinerTransactionDtoDataBase {

    private Logger logger = LoggerFactory.getLogger(MinerTransactionDtoDtoDataBaseDefaultImpl.class);

    private final static String MinerTransaction_DataBase_DirectName = "MinerTransactionDtoDataBase";
    private DB transactionPoolDB;
    private TransactionDataBase transactionDataBase;

    public MinerTransactionDtoDtoDataBaseDefaultImpl(String blockchainDataPath, TransactionDataBase transactionDataBase) throws Exception {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(blockchainDataPath,MinerTransaction_DataBase_DirectName));
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
    public List<TransactionDTO> selectTransactionDtoList(BlockChainDataBase blockChainDataBase,int from, int size) throws Exception {
        synchronized (BlockChainDataBase.class){
            List<TransactionDTO> transactionDtoList = new ArrayList<>();
            DBIterator dbIterator = this.transactionPoolDB.iterator();
            int index = 0;
            while (dbIterator.hasNext()){
                if(index>=from && from<from+size){
                    Map.Entry<byte[],byte[]> entry =  dbIterator.next();
                    byte[] byteKey = entry.getKey();
                    byte[] byteTransaction = entry.getValue();
                    TransactionDTO transactionDTO = null;
                    try {
                        transactionDTO = decodeToTransactionDTO(byteTransaction);
                        transactionDtoList.add(transactionDTO);
                    } catch (Exception e) {
                        //TODO 删除交易
                        logger.error("反序列出错",e);
                        LevelDBUtil.delete(transactionPoolDB,byteKey);
                    }
                } else {
                    break;
                }
                index++;
            }
            return transactionDtoList;
        }
    }

    @Override
    public void deleteTransactionDtoByTransactionUUID(String transactionUUID) throws Exception {
        LevelDBUtil.delete(transactionPoolDB,transactionUUID);
    }

    @Override
    public void deleteTransactionDtoListByTransactionUuidList(List<String> transactionUuidList) throws Exception {
        if(transactionUuidList == null || transactionUuidList.size()==0){
            return;
        }
        WriteBatch writeBatch = new WriteBatchImpl();
        for(String transactionUuid:transactionUuidList){
            writeBatch.delete(LevelDBUtil.stringToBytes(transactionUuid));
        }
        LevelDBUtil.write(transactionPoolDB,writeBatch);
    }

    @Override
    public TransactionDTO selectTransactionDtoByTransactionUUID(String transactionUUID) throws Exception {
        byte[] byteTransactionDTO = LevelDBUtil.get(transactionPoolDB,transactionUUID);
        if(byteTransactionDTO == null){
            return null;
        }
        return decodeToTransactionDTO(byteTransactionDTO);
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
}
