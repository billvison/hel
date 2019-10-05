package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class EncodeDecode {

    public static byte[] encode(Transaction transaction) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(transaction);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    public static Transaction decodeToTransaction(byte[] encode) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encode);
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
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    public static TransactionOutput decodeToTransactionOutput(byte[] encode) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encode);
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
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    public static Block decodeToBlock(byte[] encode) throws Exception{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encode);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Block block = (Block) objectInputStream.readObject();
        return block;
    }
}
