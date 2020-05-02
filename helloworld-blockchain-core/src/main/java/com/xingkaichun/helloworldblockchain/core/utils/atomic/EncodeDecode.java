package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * EncodeDecode工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class EncodeDecode {

    public static byte[] encode(Transaction transaction) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(transaction);
        byte[] bytesTransaction = byteArrayOutputStream.toByteArray();
        return bytesTransaction;
    }

    public static Transaction decodeToTransaction(byte[] bytesTransaction) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesTransaction);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Transaction transaction = (Transaction) objectInputStream.readObject();
        return transaction;
    }

    public static byte[] encode(TransactionOutput transactionOutput) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(transactionOutput);
        byte[] bytesTransactionOutput = byteArrayOutputStream.toByteArray();
        return bytesTransactionOutput;
    }

    public static TransactionOutput decodeToTransactionOutput(byte[] bytesTransactionOutput) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesTransactionOutput);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        TransactionOutput transactionOutput = (TransactionOutput) objectInputStream.readObject();
        return transactionOutput;
    }

    public static byte[] encode(Block block) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(block);
        byte[] bytesBlock = byteArrayOutputStream.toByteArray();
        return bytesBlock;
    }

    public static Block decodeToBlock(byte[] bytesBlock) throws Exception{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesBlock);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Block block = (Block) objectInputStream.readObject();
        return block;
    }

    public static byte[] encode(List<TransactionOutput> transactionOutputList) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(transactionOutputList);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    public static List<TransactionOutput> decodeToTransactionOutputList(byte[] byteTransactionOutputList) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteTransactionOutputList);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        List<TransactionOutput> transactionOutputList = (List<TransactionOutput>) objectInputStream.readObject();
        return transactionOutputList;
    }
}
