package com.xingkaichun.helloworldblockchain.node.plugins;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.listen.BlockChainActionData;
import com.xingkaichun.helloworldblockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.helloworldblockchain.model.Block;
import com.xingkaichun.helloworldblockchain.model.enums.BlockChainActionEnum;
import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.LevelDBUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AddressUtxoPlugin {

    private DB addressUtxoDB;

    private BlockChainCore blockChainCore;
    private BlockChainDataBase blockChainDataBase;

    private Lock lock = new ReentrantLock();

    private String blockchainDataPath;
    private static final String DATABASE_DIRECT = "PluginAddressUtxoDatabase";

    public AddressUtxoPlugin(String blockchainDataPath, BlockChainCore blockChainCore) throws Exception {

        this.blockChainCore = blockChainCore;
        this.blockChainDataBase = blockChainCore.getBlockChainDataBase();
        this.blockchainDataPath = blockchainDataPath;

        init_DB();

        blockChainCore.registerBlockChainActionListener(new BlockChainActionListener() {

            @Override
            public void addOrDeleteBlock(List<BlockChainActionData> list) {
                for(BlockChainActionData blockChainActionData:list){
                    try {
                        //TODO 是否应该程序停止
                        changeDB(blockChainActionData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                addressUtxoDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }


    private void init_DB() throws Exception {
        File addressUtxoDBDir = new File(blockchainDataPath,DATABASE_DIRECT);
        FileUtils.deleteDirectory(addressUtxoDBDir);
        this.addressUtxoDB = LevelDBUtil.createDB(addressUtxoDBDir);
        Block lastBlock = blockChainDataBase.findTailBlock();
        if(lastBlock==null){
            return;
        }
        for(int blockHeight = BlockChainCoreConstants.FIRST_BLOCK_HEIGHT; blockHeight<=lastBlock.getHeight(); blockHeight++){
            Block block = blockChainDataBase.findBlockByBlockHeight(blockHeight);
            BlockChainActionData blockChainActionData = new BlockChainActionData();
            blockChainActionData.setBlockChainActionEnum(BlockChainActionEnum.ADD_BLOCK);
            List<Block> blockList = new ArrayList<>();
            blockList.add(block);
            blockChainActionData.setBlockList(blockList);
            changeDB(blockChainActionData);
        }
    }

    private void changeDB(BlockChainActionData blockChainActionData) throws Exception {
        List<Block> blockList = blockChainActionData.getBlockList();
        lock.lock();
        try{
            for(Block block:blockList){
                Map<String,List<TransactionOutput>> addressUtxoList = new HashMap<>();
                Map<String,List<TransactionOutput>> addressTxoList = new HashMap<>();

                for(Transaction transaction : block.getTransactions()){
                    List<TransactionInput> inputs = transaction.getInputs();
                    if(inputs!=null){
                        for (TransactionInput transactionInput:inputs){
                            TransactionOutput utxo = transactionInput.getUnspendTransactionOutput();
                            List<TransactionOutput> txList = addUTxoList(addressUtxoList,utxo);
                            //区块数据
                            if(blockChainActionData.getBlockChainActionEnum() == BlockChainActionEnum.ADD_BLOCK){
                                txList.removeIf(txo -> txo.getTransactionOutputUUID().equals(utxo.getTransactionOutputUUID()));
                            }else{
                                txList.add(utxo);
                            }
                        }
                    }
                    List<TransactionOutput> outputs = transaction.getOutputs();
                    for (TransactionOutput transactionOutput:outputs){
                        List<TransactionOutput> txList = addUTxoList(addressUtxoList,transactionOutput);
                        if(blockChainActionData.getBlockChainActionEnum() == BlockChainActionEnum.ADD_BLOCK){
                            txList.add(transactionOutput);
                        }else{
                            txList.removeIf(txo -> txo.getTransactionOutputUUID().equals(transactionOutput.getTransactionOutputUUID()));
                        }
                        List<TransactionOutput> txoList = addTxoList(addressTxoList,transactionOutput);
                        if(blockChainActionData.getBlockChainActionEnum() == BlockChainActionEnum.ADD_BLOCK){
                            txoList.add(transactionOutput);
                        }else{
                            txoList.removeIf(txo -> txo.getTransactionOutputUUID().equals(transactionOutput.getTransactionOutputUUID()));
                        }
                    }
                }

                WriteBatch writeBatch1 = new WriteBatchImpl();

                for(Map.Entry<String,List<TransactionOutput>> entry:addressUtxoList.entrySet()){
                    String address = entry.getKey();
                    List<TransactionOutput> utxoList = entry.getValue();
                    writeBatch1.put(toBytes(addUtxoPrefix(address)),encode(utxoList));
                }

                for(Map.Entry<String,List<TransactionOutput>> entry:addressTxoList.entrySet()){
                    String address = entry.getKey();
                    List<TransactionOutput> txoList = entry.getValue();
                    writeBatch1.put(buildTxoKey(address),encode(txoList));
                }

                addressUtxoDB.write(writeBatch1);
            }
        }finally {
            lock.unlock();
        }
    }

    private List<TransactionOutput> addUTxoList(Map<String, List<TransactionOutput>> addressUtxoList, TransactionOutput transactionOutput) throws Exception {
        StringAddress stringAddress = transactionOutput.getStringAddress();
        List<TransactionOutput> transactionOutputList = addressUtxoList.get(stringAddress.getValue());
        if(transactionOutputList==null){
            byte[] byteUtxo = addressUtxoDB.get(toBytes(addUtxoPrefix(stringAddress.getValue())));
            if(byteUtxo==null){
                transactionOutputList = new ArrayList<>();
            }else {
                transactionOutputList = decodeToBlock(byteUtxo);
            }
            addressUtxoList.put(stringAddress.getValue(),transactionOutputList);
        }
        return transactionOutputList;
    }

    private List<TransactionOutput> addTxoList(Map<String, List<TransactionOutput>> addressUtxoList, TransactionOutput transactionOutput) throws Exception {
        StringAddress stringAddress = transactionOutput.getStringAddress();
        List<TransactionOutput> transactionOutputList = addressUtxoList.get(stringAddress.getValue());
        if(transactionOutputList==null){
            byte[] byteUtxo = addressUtxoDB.get(buildTxoKey(stringAddress.getValue()));
            if(byteUtxo==null){
                transactionOutputList = new ArrayList<>();
            }else {
                transactionOutputList = decodeToBlock(byteUtxo);
            }
            addressUtxoList.put(stringAddress.getValue(),transactionOutputList);
        }
        return transactionOutputList;
    }

    private byte[] toBytes(String str){
        return str.getBytes(Charset.forName("UTF-8"));
    }

    private byte[] buildTxoKey(String address) {
        String stringKey = "TXO_"+address;
        return toBytes(stringKey);
    }

    private String addUtxoPrefix(String address) {
        return "UTXO_"+ address;
    }

    private static byte[] encode(List<TransactionOutput> block) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(block);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    private static List<TransactionOutput> decodeToBlock(byte[] encode) throws Exception{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encode);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        List<TransactionOutput> transactionOutputList = (List<TransactionOutput>) objectInputStream.readObject();
        return transactionOutputList;
    }

    public List<TransactionOutput> queryUtxoListByAddress(String address) throws Exception {
        byte[] byteUtxoList = addressUtxoDB.get(buildTxoKey(address));
        if(byteUtxoList==null){
            return null;
        }
        return decodeToBlock(byteUtxoList);
    }
}
