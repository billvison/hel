package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.core.UnconfirmedTransactionDatabase;
import com.xingkaichun.helloworldblockchain.core.tools.EncodeDecodeTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class UnconfirmedTransactionDatabaseDefaultImpl extends UnconfirmedTransactionDatabase {

    private static final String UNCONFIRMED_TRANSACTION_DATABASE_NAME = "UnconfirmedTransactionDatabase";
    private final String unconfirmedTransactionDatabasePath;

    public UnconfirmedTransactionDatabaseDefaultImpl(CoreConfiguration coreConfiguration) {
        this.unconfirmedTransactionDatabasePath = FileUtil.newPath(coreConfiguration.getCorePath(), UNCONFIRMED_TRANSACTION_DATABASE_NAME);
    }

    @Override
    public void insertTransaction(TransactionDto transactionDto) {
        //交易已经持久化进交易池数据库 丢弃交易
        synchronized (UnconfirmedTransactionDatabaseDefaultImpl.class){
            String transactionHash = TransactionTool.calculateTransactionHash(transactionDto);
            KvDBUtil.put(unconfirmedTransactionDatabasePath, getKey(transactionHash), EncodeDecodeTool.encode(transactionDto));
        }
    }

    @Override
    public List<TransactionDto> selectTransactions(long from, long size) {
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        List<byte[]> bytesTransactionDtos = KvDBUtil.get(unconfirmedTransactionDatabasePath,from,size);
        if(bytesTransactionDtos != null){
            for(byte[] bytesTransactionDto:bytesTransactionDtos){
                TransactionDto transactionDto = EncodeDecodeTool.decodeToTransactionDto(bytesTransactionDto);
                transactionDtoList.add(transactionDto);
            }
        }
        return transactionDtoList;
    }

    @Override
    public void deleteByTransactionHash(String transactionHash) {
        KvDBUtil.delete(unconfirmedTransactionDatabasePath, getKey(transactionHash));
    }

    @Override
    public TransactionDto selectTransactionByTransactionHash(String transactionHash) {
        byte[] byteTransactionDto = KvDBUtil.get(unconfirmedTransactionDatabasePath, getKey(transactionHash));
        if(byteTransactionDto == null){
            return null;
        }
        return EncodeDecodeTool.decodeToTransactionDto(byteTransactionDto);
    }

    private byte[] getKey(String transactionHash){
        return ByteUtil.stringToUtf8Bytes(transactionHash);
    }
}
