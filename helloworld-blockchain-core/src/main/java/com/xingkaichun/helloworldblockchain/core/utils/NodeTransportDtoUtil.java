package com.xingkaichun.helloworldblockchain.core.utils;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptKey;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptLock;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.KeyUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionOutputDTO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 节点传输工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class NodeTransportDtoUtil {

    private static Gson gson = new Gson();
    /**
     * 类型转换
     */
    public static Block classCast(BlockChainDataBase blockChainDataBase, BlockDTO blockDTO) throws Exception {
        if(BigIntegerUtil.isLessThan(blockDTO.getHeight(),BigInteger.ONE)){
            throw new BlockChainCoreException("区块的高度不能少于1");
        }

        List<Transaction> transactionList = new ArrayList<>();
        List<TransactionDTO> transactionDtoList = blockDTO.getTransactions();
        if(transactionDtoList != null){
            for(TransactionDTO transactionDTO:transactionDtoList){
                Transaction transaction = classCast(blockChainDataBase,transactionDTO);
                transactionList.add(transaction);
            }
        }

        //求上一个区块的hash
        String previousHash = null;
        BigInteger height = blockDTO.getHeight();
        if(BigIntegerUtil.isEquals(height,BigInteger.ONE)){
            previousHash = BlockChainCoreConstant.FIRST_BLOCK_PREVIOUS_HASH;
        } else {
            Block previousBlock = blockChainDataBase.findNoTransactionBlockByBlockHeight(height.subtract(BigInteger.ONE));
            if(previousBlock == null){
                throw new BlockChainCoreException("上一个区块不应该为null");
            }
            previousHash = previousBlock.getHash();
        }

        Block block = new Block();
        block.setTimestamp(blockDTO.getTimestamp());
        block.setPreviousHash(previousHash);
        block.setHeight(blockDTO.getHeight());
        block.setTransactions(transactionList);
        block.setMerkleRoot(BlockUtil.calculateBlockMerkleRoot(block));
        block.setConsensusValue(blockDTO.getConsensusValue());
        block.setHash(BlockUtil.calculateBlockHash(block));
        block.setConsensusVariableHolder(blockChainDataBase.getConsensus().calculateConsensusVariableHolder(blockChainDataBase,block));
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
        blockDTO.setHeight(block.getHeight());
        blockDTO.setTransactions(transactionDtoList);
        blockDTO.setConsensusValue(block.getConsensusValue());
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
                String unspendTransactionOutputHash = transactionInputDTO.getUnspendTransactionOutputHash();
                TransactionOutput transactionOutput = blockChainDataBase.findUnspendTransactionOuputByTransactionOuputHash(unspendTransactionOutputHash);
                if(transactionOutput == null){
                    throw new BlockChainCoreException("TransactionOutput不应该是null。");
                }
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setUnspendTransactionOutput(transactionOutput);
                transactionInput.setScriptKey(scriptKeyFrom(transactionInputDTO.getScriptKey()));
                inputs.add(transactionInput);
            }
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        List<TransactionOutputDTO> dtoOutputs = transactionDTO.getOutputs();
        if(dtoOutputs!=null && dtoOutputs.size()!=0){
            for(TransactionOutputDTO transactionOutputDTO:dtoOutputs){
                TransactionOutput transactionOutput = classCast(transactionDTO,transactionOutputDTO);
                outputs.add(transactionOutput);
            }
        }

        Transaction transaction = new Transaction();
        transaction.setTimestamp(transactionDTO.getTimestamp());
        TransactionType transactionType = transactionTypeFromTransactionDTO(transactionDTO);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionHash(BlockchainHashUtil.calculateTransactionHash(transactionDTO));
        transaction.setInputs(inputs);
        transaction.setOutputs(outputs);
        transaction.setMessages(transactionDTO.getMessages());
        return transaction;
    }

    private static TransactionType transactionTypeFromTransactionDTO(TransactionDTO transactionDTO) {
        for(TransactionType transactionType:TransactionType.values()){
            if(transactionType.getCode() == transactionDTO.getTransactionTypeCode()){
                return transactionType;
            }
        }
        throw new BlockChainCoreException("交易类型不存在");
    }

    private static ScriptKey scriptKeyFrom(List<String> scriptKey) {
        if(scriptKey == null){
            return null;
        }
        ScriptKey sKey = new ScriptKey();
        for(String script:scriptKey){
            sKey.add(script);
        }
        return sKey;
    }

    private static ScriptLock scriptLockFrom(List<String> scriptLock) {
        if(scriptLock == null){
            return null;
        }
        ScriptLock sLock = new ScriptLock();
        for(String script:scriptLock){
            sLock.add(script);
        }
        return sLock;
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
                transactionInputDTO.setUnspendTransactionOutputHash(unspendTransactionOutput.getTransactionOutputHash());
                transactionInputDTO.setScriptKey(transactionInput.getScriptKey());
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
        transactionDTO.setTransactionTypeCode(transaction.getTransactionType().getCode());
        transactionDTO.setInputs(inputs);
        transactionDTO.setOutputs(outputs);
        transactionDTO.setMessages(transaction.getMessages());
        return transactionDTO;
    }

    /**
     * 类型转换
     */
    public static TransactionOutput classCast(TransactionDTO transactionDTO, TransactionOutputDTO transactionOutputDTO) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionOutputHash(BlockchainHashUtil.calculateTransactionOutputHash(transactionDTO,transactionOutputDTO));
        transactionOutput.setStringAddress(new StringAddress(transactionOutputDTO.getAddress()));
        transactionOutput.setValue(new BigDecimal(transactionOutputDTO.getValue()));
        transactionOutput.setScriptLock(scriptLockFrom(transactionOutputDTO.getScriptLock()));
        return transactionOutput;
    }

    /**
     * 类型转换
     */
    public static TransactionOutputDTO classCast(TransactionOutput transactionOutput) {
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setAddress(transactionOutput.getStringAddress().getValue());
        transactionOutputDTO.setValue(transactionOutput.getValue().toPlainString());
        transactionOutputDTO.setScriptLock(transactionOutput.getScriptLock());
        return transactionOutputDTO;
    }

    /**
     * 交易签名
     */
    public static String signature(TransactionDTO transactionDTO, StringPrivateKey stringPrivateKey) {
        String strSignature = KeyUtil.signature(stringPrivateKey,signatureData(transactionDTO));
        return strSignature;
    }

    /**
     * 用于签名的数据数据
     */
    public static String signatureData(TransactionDTO transactionDTO) {
        String data = BlockchainHashUtil.calculateTransactionHash(transactionDTO);
        return data;
    }

    /**
     * 编码 BlockDTO
     */
    public static String encode(BlockDTO blockDTO) {
        return gson.toJson(blockDTO);
    }

    /**
     * 解码 BlockDTO
     */
    public static BlockDTO decodeToBlockDTO(String stringBlockDTO) {
        return gson.fromJson(stringBlockDTO,BlockDTO.class);
    }
}
