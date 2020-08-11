package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.core.utils.EncodeDecodeUtil;
import com.xingkaichun.helloworldblockchain.core.utils.LevelDBUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class MinerTransactionDtoDtoDataBaseDefaultImpl extends MinerTransactionDtoDataBase {

    private Logger logger = LoggerFactory.getLogger(MinerTransactionDtoDtoDataBaseDefaultImpl.class);

    private final static String MinerTransaction_DataBase_DirectName = "MinerTransactionDtoDataBase";
    private DB transactionPoolDB;

    public MinerTransactionDtoDtoDataBaseDefaultImpl(String blockchainDataPath) {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(blockchainDataPath,MinerTransaction_DataBase_DirectName));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                transactionPoolDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void insertTransactionDTO(TransactionDTO transactionDTO) {
        //交易已经持久化进交易池数据库 丢弃交易
        synchronized (this.getClass()){
            String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
            LevelDBUtil.put(transactionPoolDB,transactionHash, EncodeDecodeUtil.encode(transactionDTO));
        }
    }

    @Override
    public List<TransactionDTO> selectTransactionDtoList(long from, long size) {
        synchronized (this.getClass()){
            List<TransactionDTO> transactionDtoList = new ArrayList<>();
            int cunrrentFrom = 0;
            int cunrrentSize = 0;
            for (DBIterator iterator = this.transactionPoolDB.iterator(); iterator.hasNext(); iterator.next()) {
                byte[] byteValue = iterator.peekNext().getValue();
                if(byteValue == null || byteValue.length==0){
                    continue;
                }
                cunrrentFrom++;
                if(cunrrentFrom>=from && cunrrentSize<size){
                    TransactionDTO transactionDTO = EncodeDecodeUtil.decodeToTransactionDTO(byteValue);
                    transactionDtoList.add(transactionDTO);
                    cunrrentSize++;
                }
                if(cunrrentSize>=size){
                    break;
                }
            }
            return transactionDtoList;
        }
    }

    @Override
    public void deleteTransactionDto(TransactionDTO transactionDTO) {
        String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
        LevelDBUtil.delete(transactionPoolDB,transactionHash);
    }

    @Override
    public void deleteTransactionDtoListByTransactionHashList(List<String> transactionHashList) {
        if(transactionHashList == null || transactionHashList.size()==0){
            return;
        }
        WriteBatch writeBatch = new WriteBatchImpl();
        for(String transactionHash:transactionHashList){
            writeBatch.delete(LevelDBUtil.stringToBytes(transactionHash));
        }
        LevelDBUtil.write(transactionPoolDB,writeBatch);
    }

    @Override
    public TransactionDTO selectTransactionDtoByTransactionHash(String transactionHash) {
        byte[] byteTransactionDTO = LevelDBUtil.get(transactionPoolDB,transactionHash);
        if(byteTransactionDTO == null){
            return null;
        }
        return EncodeDecodeUtil.decodeToTransactionDTO(byteTransactionDTO);
    }
}
