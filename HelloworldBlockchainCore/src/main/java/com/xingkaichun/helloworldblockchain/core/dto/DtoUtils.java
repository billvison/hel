package com.xingkaichun.helloworldblockchain.core.dto;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPublicKey;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.CipherUtil;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.KeyUtil;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.TransactionUtil;

import java.io.*;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DtoUtils {

    /**
     * 类型转换
     */
    public static Block classCast(BlockChainDataBase blockChainDataBase, BlockDTO blockDTO) throws Exception {
        List<Transaction> transactionList = new ArrayList<>();
        List<TransactionDTO> transactionDtoList = blockDTO.getTransactions();
        if(transactionDtoList != null){
            for(TransactionDTO transactionDTO:transactionDtoList){
                Transaction transaction = classCast(blockChainDataBase,transactionDTO);
                transactionList.add(transaction);
            }
        }

        Block block = new Block();
        block.setTimestamp(blockDTO.getTimestamp());
        block.setPreviousHash(blockDTO.getPreviousHash());
        block.setHeight(blockDTO.getHeight());
        block.setTransactions(transactionList);
        block.setMerkleRoot(blockDTO.getMerkleRoot());
        block.setNonce(blockDTO.getNonce());
        block.setHash(blockDTO.getHash());
        return block;
    }
    /**
     * 类型转换
     */
    public static BlockDTO classCast(Block block) throws Exception {
        if(block == null){
            return null;
        }
        List<TransactionDTO> transactionDtoList = new ArrayList<>();
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                TransactionDTO transactionDTO = classCast(transaction);
                transactionDtoList.add(transactionDTO);
            }
        }

        BlockDTO blockDTO = new BlockDTO();
        blockDTO.setTimestamp(block.getTimestamp());
        blockDTO.setPreviousHash(block.getPreviousHash());
        blockDTO.setHeight(block.getHeight());
        blockDTO.setTransactions(transactionDtoList);
        blockDTO.setMerkleRoot(block.getMerkleRoot());
        blockDTO.setNonce(block.getNonce());
        blockDTO.setHash(block.getHash());
        return blockDTO;
    }

    /**
     * 类型转换
     */
    public static Transaction classCast(BlockChainDataBase blockChainDataBase, TransactionDTO transactionDTO) throws Exception {
        List<TransactionInput> inputs = new ArrayList<>();
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getInputs();
        if(transactionInputDtoList!=null || transactionInputDtoList.size()!=0){
            for (TransactionInputDTO transactionInputDTO:transactionInputDtoList){
                String unspendTransactionOutputUUID = transactionInputDTO.getUnspendTransactionOutputUUID();
                TransactionOutput transactionOutput = blockChainDataBase.findUtxoByUtxoUuid(unspendTransactionOutputUUID);
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setStringPublicKey(new StringPublicKey(transactionInputDTO.getPublicKey()));
                transactionInput.setUnspendTransactionOutput(transactionOutput);
                inputs.add(transactionInput);
            }
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        List<TransactionOutputDTO> dtoOutputs = transactionDTO.getOutputs();
        if(dtoOutputs!=null || dtoOutputs.size()!=0){
            for(TransactionOutputDTO transactionOutputDTO:dtoOutputs){
                TransactionOutput transactionOutput = classCast(transactionOutputDTO);
                outputs.add(transactionOutput);
            }
        }

        Transaction transaction = new Transaction();
        transaction.setTimestamp(transaction.getTimestamp());
        transaction.setTransactionUUID(transactionDTO.getTransactionUUID());
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setInputs(inputs);
        transaction.setOutputs(outputs);
        transaction.setSignature(transactionDTO.getSignature());
        return transaction;
    }
    /**
     * 类型转换
     */
    public static TransactionDTO classCast(Transaction transaction) throws Exception {
        List<TransactionInputDTO> inputs = new ArrayList<>();
        List<TransactionInput> transactionInputList = transaction.getInputs();
        if(transactionInputList!=null || transactionInputList.size()!=0){
            for (TransactionInput transactionInput:transactionInputList){
                TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
                TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
                transactionInputDTO.setUnspendTransactionOutputUUID(unspendTransactionOutput.getTransactionOutputUUID());
                transactionInputDTO.setPublicKey(transactionInput.getStringPublicKey().getValue());
                inputs.add(transactionInputDTO);
            }
        }

        List<TransactionOutputDTO> outputs = new ArrayList<>();
        List<TransactionOutput> transactionOutputList = transaction.getOutputs();
        if(transactionOutputList!=null || transactionOutputList.size()!=0){
            for(TransactionOutput transactionOutput:transactionOutputList){
                TransactionOutputDTO transactionOutputDTO = classCast(transactionOutput);
                outputs.add(transactionOutputDTO);
            }
        }

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(transaction.getTimestamp());
        transactionDTO.setTransactionUUID(transaction.getTransactionUUID());
        transactionDTO.setTransactionType(transaction.getTransactionType());
        transactionDTO.setInputs(inputs);
        transactionDTO.setOutputs(outputs);
        transactionDTO.setSignature(transaction.getSignature());
        return transactionDTO;
    }

    /**
     * 类型转换
     */
    public static TransactionOutput classCast(TransactionOutputDTO transactionOutputDTO) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionOutputUUID(transactionOutputDTO.getTransactionOutputUUID());
        transactionOutput.setStringAddress(new StringAddress(transactionOutputDTO.getAddress()));
        transactionOutput.setValue(transactionOutputDTO.getValue());
        return transactionOutput;
    }
    /**
     * 类型转换
     */
    public static TransactionOutputDTO classCast(TransactionOutput transactionOutput) {
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setTransactionOutputUUID(transactionOutputDTO.getTransactionOutputUUID());
        transactionOutputDTO.setAddress(transactionOutput.getStringAddress().getValue());
        transactionOutputDTO.setValue(transactionOutputDTO.getValue());
        return transactionOutputDTO;
    }

    /**
     * 类型转换
     */
    public static WalletDTO classCast(Wallet wallet){
        WalletDTO walletDTO = new WalletDTO(wallet.getStringPrivateKey().getValue(),wallet.getStringPublicKey().getValue(),wallet.getStringAddress().getValue());
        return walletDTO;
    }
    /**
     * 类型转换
     */
    public static Wallet classCast(WalletDTO walletDTO){
        Wallet wallet = new Wallet(new StringPrivateKey(walletDTO.getPrivateKey()),
                new StringPublicKey(walletDTO.getPublicKey()),
                new StringAddress(walletDTO.getAddress()));
        return wallet;
    }

    /**
     * 交易签名
     */
    public static String signature(TransactionDTO transactionDTO, StringPrivateKey stringPrivateKey) throws Exception {
        PrivateKey privateKey = KeyUtil.convertStringPrivateKeyToPrivateKey(stringPrivateKey);
        byte[] bytesSignature = CipherUtil.applyECDSASig(privateKey,signatureData(transactionDTO));
        String strSignature = Base64.getEncoder().encodeToString(bytesSignature);
        return strSignature;
    }

    /**
     * 用于签名的数据数据
     */
    public static String signatureData(TransactionDTO transactionDTO) throws Exception {
        String data = TransactionUtil.signatureData(transactionDTO.getTimestamp(),transactionDTO.getTransactionUUID(),getInputUtxoIds(transactionDTO),getOutpuUtxoIds(transactionDTO));
        String sha256Data = CipherUtil.applySha256(data);
        return sha256Data;
    }

    private static List<String> getInputUtxoIds(TransactionDTO transactionDTO){
        List<String> ids = new ArrayList<>();
        List<TransactionInputDTO> inputs = transactionDTO.getInputs();
        if(inputs==null || inputs.size()==0){
            return ids;
        }
        for(TransactionInputDTO transactionInputDTO:inputs){
            ids.add(transactionInputDTO.getUnspendTransactionOutputUUID());
        }
        return ids;
    }

    private static List<String> getOutpuUtxoIds(TransactionDTO transactionDTO){
        List<String> ids = new ArrayList<>();
        List<TransactionOutputDTO> output = transactionDTO.getOutputs();
        if(output==null || output.size()==0){
            return ids;
        }
        for(TransactionOutputDTO transactionOutputDTO:output){
            ids.add(transactionOutputDTO.getTransactionOutputUUID());
        }
        return ids;
    }

    public static byte[] encode(BlockDTO blockDTO) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(blockDTO);
        byte[] bytesBlockDTO = byteArrayOutputStream.toByteArray();
        return bytesBlockDTO;
    }

    public static BlockDTO decodeToBlockDTO(byte[] byteBlockDTO) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBlockDTO);
        ObjectInputStream objectInputStream = null;
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        BlockDTO blockDTO = (BlockDTO) objectInputStream.readObject();
        return blockDTO;
    }

}
