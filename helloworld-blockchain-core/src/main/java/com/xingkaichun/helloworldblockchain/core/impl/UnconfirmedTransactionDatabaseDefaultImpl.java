package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.UnconfirmedTransactionDatabase;
import com.xingkaichun.helloworldblockchain.core.tools.EncodeDecodeTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
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
    private String unconfirmedTransactionDatabasePath = null;

    public UnconfirmedTransactionDatabaseDefaultImpl(String rootPath) {
        this.unconfirmedTransactionDatabasePath = FileUtil.newPath(rootPath, UNCONFIRMED_TRANSACTION_DATABASE_NAME);
    }

    public void insertTransactionDTO(TransactionDTO transactionDTO) {
        //交易已经持久化进交易池数据库 丢弃交易
        synchronized (UnconfirmedTransactionDatabaseDefaultImpl.class){
            String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
            KvDBUtil.put(unconfirmedTransactionDatabasePath, getKey(transactionHash), EncodeDecodeTool.encode(transactionDTO));
        }
    }

    @Override
    public List<TransactionDTO> selectTransactionDtoList(long from, long size) {
        List<TransactionDTO> transactionDtoList = new ArrayList<>();
        List<byte[]> bytesTransactionDTOList = KvDBUtil.get(unconfirmedTransactionDatabasePath,from,size);
        if(bytesTransactionDTOList != null){
            for(byte[] bytesTransactionDTO:bytesTransactionDTOList){
                TransactionDTO transactionDTO = EncodeDecodeTool.decodeToTransactionDTO(bytesTransactionDTO);
                transactionDtoList.add(transactionDTO);
            }
        }
        return transactionDtoList;
    }

    @Override
    public void deleteByTransactionHash(String transactionHash) {
        KvDBUtil.delete(unconfirmedTransactionDatabasePath, getKey(transactionHash));
    }

    @Override
    public TransactionDTO selectTransactionDtoByTransactionHash(String transactionHash) {
        byte[] byteTransactionDTO = KvDBUtil.get(unconfirmedTransactionDatabasePath, getKey(transactionHash));
        if(byteTransactionDTO == null){
            return null;
        }
        return EncodeDecodeTool.decodeToTransactionDTO(byteTransactionDTO);
    }

    private byte[] getKey(String transactionHash){
        return ByteUtil.encode(transactionHash);
    }
}
