package com.xingkaichun.helloworldblockchain.core.utils;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.key.Wallet;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.TransactionUtil;
import com.xingkaichun.helloworldblockchain.crypto.KeyUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import com.xingkaichun.helloworldblockchain.node.transport.dto.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NodeTransportUtils {

    private static Gson gson = new Gson();
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
        //TODO 缩减dto字段 用区块链系统直接计算可以计算出来的值
        Block block = new Block();
        block.setTimestamp(blockDTO.getTimestamp());
        block.setPreviousHash(blockDTO.getPreviousHash());
        block.setHeight(blockDTO.getHeight());
        block.setTransactions(transactionList);
        block.setMerkleRoot(blockDTO.getMerkleRoot());
        block.setNonce(blockDTO.getNonce());
        block.setHash(blockDTO.getHash());
        block.setConsensusTarget(blockChainDataBase.getConsensus().calculateConsensusTarget(blockChainDataBase,block));
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
        if(transactionInputDtoList!=null){
            for (TransactionInputDTO transactionInputDTO:transactionInputDtoList){
                String unspendTransactionOutputUUID = transactionInputDTO.getUnspendTransactionOutputUUID();
                TransactionOutput transactionOutput = blockChainDataBase.findUtxoByUtxoUuid(unspendTransactionOutputUUID);
                if(transactionOutput == null){
                    throw new BlockChainCoreException("TransactionOutput不应该是null。");
                }
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setStringPublicKey(new StringPublicKey(transactionInputDTO.getPublicKey()));
                transactionInput.setUnspendTransactionOutput(transactionOutput);
                inputs.add(transactionInput);
            }
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        List<TransactionOutputDTO> dtoOutputs = transactionDTO.getOutputs();
        if(dtoOutputs!=null && dtoOutputs.size()!=0){
            for(TransactionOutputDTO transactionOutputDTO:dtoOutputs){
                TransactionOutput transactionOutput = classCast(transactionOutputDTO);
                outputs.add(transactionOutput);
            }
        }

        String transactionTypeDto = transactionDTO.getTransactionType();
        Transaction transaction = new Transaction();
        transaction.setTimestamp(transactionDTO.getTimestamp());
        transaction.setTransactionUUID(transactionDTO.getTransactionUUID());
        transaction.setTransactionType(TransactionType.valueOf(transactionTypeDto));
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
        if(transactionInputList!=null){
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
        if(transactionOutputList!=null){
            for(TransactionOutput transactionOutput:transactionOutputList){
                TransactionOutputDTO transactionOutputDTO = classCast(transactionOutput);
                outputs.add(transactionOutputDTO);
            }
        }

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(transaction.getTimestamp());
        transactionDTO.setTransactionUUID(transaction.getTransactionUUID());
        transactionDTO.setTransactionType(transaction.getTransactionType().name());
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
        transactionOutput.setValue(new BigDecimal(transactionOutputDTO.getValue()));
        return transactionOutput;
    }
    /**
     * 类型转换
     */
    public static TransactionOutputDTO classCast(TransactionOutput transactionOutput) {
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setTransactionOutputUUID(transactionOutput.getTransactionOutputUUID());
        transactionOutputDTO.setAddress(transactionOutput.getStringAddress().getValue());
        transactionOutputDTO.setValue(transactionOutput.getValue().toPlainString());
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
        String strSignature = KeyUtil.applyECDSASig(stringPrivateKey,signatureData(transactionDTO));
        return strSignature;
    }

    /**
     * 用于签名的数据数据
     */
    public static String signatureData(TransactionDTO transactionDTO) throws Exception {
        String data = TransactionUtil.signatureData(transactionDTO.getTimestamp(),transactionDTO.getTransactionUUID(),getInputUtxoIds(transactionDTO),getOutpuUtxoIds(transactionDTO));
        return data;
    }

    private static List<String> getInputUtxoIds(TransactionDTO transactionDTO){
        List<String> ids = new ArrayList<>();
        List<TransactionInputDTO> inputs = transactionDTO.getInputs();
        if(inputs == null){
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
        if(output == null){
            return ids;
        }
        for(TransactionOutputDTO transactionOutputDTO:output){
            ids.add(transactionOutputDTO.getTransactionOutputUUID());
        }
        return ids;
    }

    public static String encode(BlockDTO blockDTO) throws IOException {
        return gson.toJson(blockDTO);
    }

    public static BlockDTO decodeToBlockDTO(String stringBlockDTO) throws IOException, ClassNotFoundException {
        return gson.fromJson(stringBlockDTO,BlockDTO.class);
    }

}
