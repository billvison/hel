package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.UnconfirmedTransactionDatabase;
import com.xingkaichun.helloworldblockchain.core.tools.EncodeDecodeTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.util.LevelDBUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class UnconfirmedTransactionDatabaseDefaultImpl extends UnconfirmedTransactionDatabase {

    private static final Logger logger = LoggerFactory.getLogger(UnconfirmedTransactionDatabaseDefaultImpl.class);

    private static final String MinerTransaction_DataBase_DirectName = "UnconfirmedTransactionDatabase";
    private DB transactionPoolDB;

    public UnconfirmedTransactionDatabaseDefaultImpl(String blockchainDataPath) {

        this.transactionPoolDB = LevelDBUtil.createDB(new File(blockchainDataPath,MinerTransaction_DataBase_DirectName));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LevelDBUtil.closeDB(transactionPoolDB)));
    }

    public void insertTransactionDTO(TransactionDTO transactionDTO) {
        //交易已经持久化进交易池数据库 丢弃交易
        synchronized (UnconfirmedTransactionDatabaseDefaultImpl.class){
            String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
            LevelDBUtil.put(transactionPoolDB,getKey(transactionHash), EncodeDecodeTool.encode(transactionDTO));
        }
    }

    @Override
    public List<TransactionDTO> selectTransactionDtoList(long from, long size) {
        synchronized (UnconfirmedTransactionDatabaseDefaultImpl.class){
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
                    TransactionDTO transactionDTO = EncodeDecodeTool.decodeToTransactionDTO(byteValue);
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
    public void deleteByTransactionHash(String transactionHash) {
        LevelDBUtil.delete(transactionPoolDB, getKey(transactionHash));
    }

    @Override
    public TransactionDTO selectTransactionDtoByTransactionHash(String transactionHash) {
        byte[] byteTransactionDTO = LevelDBUtil.get(transactionPoolDB,getKey(transactionHash));
        if(byteTransactionDTO == null){
            return null;
        }
        return EncodeDecodeTool.decodeToTransactionDTO(byteTransactionDTO);
    }

    private byte[] getKey(String transactionHash){
        return LevelDBUtil.stringToBytes(transactionHash);
    }
}
