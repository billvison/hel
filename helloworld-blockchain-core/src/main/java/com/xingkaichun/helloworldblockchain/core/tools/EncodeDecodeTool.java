package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

import java.io.*;

/**
 * EncodeDecode工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class EncodeDecodeTool {

    public static byte[] encode(Transaction transaction) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(transaction);
            byte[] bytesTransaction = byteArrayOutputStream.toByteArray();
            return bytesTransaction;
        } catch (IOException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }
    }
    public static Transaction decodeToTransaction(byte[] bytesTransaction) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesTransaction);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Transaction transaction = (Transaction) objectInputStream.readObject();
            return transaction;
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }
    }


    public static byte[] encode(TransactionOutput transactionOutput) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(transactionOutput);
            byte[] bytesTransactionOutput = byteArrayOutputStream.toByteArray();
            return bytesTransactionOutput;
        } catch (IOException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }
    }
    public static TransactionOutput decodeToTransactionOutput(byte[] bytesTransactionOutput) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesTransactionOutput);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            TransactionOutput transactionOutput = (TransactionOutput) objectInputStream.readObject();
            return transactionOutput;
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }

    }


    public static byte[] encode(Block block) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(block);
            byte[] bytesBlock = byteArrayOutputStream.toByteArray();
            return bytesBlock;
        } catch (IOException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }
    }
    public static Block decodeToBlock(byte[] bytesBlock) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesBlock);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Block block = (Block) objectInputStream.readObject();
            return block;
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }
    }



    public static byte[] encode(TransactionDTO transactionDTO) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(transactionDTO);
            byte[] bytesTransactionDTO = byteArrayOutputStream.toByteArray();
            return bytesTransactionDTO;
        } catch (IOException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }
    }
    public static TransactionDTO decodeToTransactionDTO(byte[] bytesTransactionDTO) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesTransactionDTO);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            TransactionDTO transactionDTO = (TransactionDTO) objectInputStream.readObject();
            return transactionDTO;
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.error("序列化/反序列化失败",e);
            throw new RuntimeException(e);
        }
    }
}
