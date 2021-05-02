package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.*;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.TimeUtil;

import java.util.*;

/**
 * 区块工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockTool {

    /**
     * 计算区块的Hash值
     */
    public static String calculateBlockHash(Block block) {
        BlockDTO blockDto = Model2DtoTool.block2BlockDTO(block);
        return calculateBlockHash(blockDto);
    }
    /**
     * 计算区块的Hash值
     */
    public static String calculateBlockHash(BlockDTO blockDto) {
        byte[] bytesTimestamp = NumberUtil.long64ToBytes64ByBigEndian(blockDto.getTimestamp());
        byte[] bytesPreviousBlockHash = HexUtil.hexStringToBytes(blockDto.getPreviousHash());
        byte[] bytesMerkleTreeRoot = HexUtil.hexStringToBytes(calculateBlockMerkleTreeRoot(blockDto));
        byte[] bytesNonce = HexUtil.hexStringToBytes(blockDto.getNonce());

        byte[] bytesInput = ByteUtil.concat(bytesTimestamp,bytesPreviousBlockHash,bytesMerkleTreeRoot,bytesNonce);
        byte[] sha256DoubleDigest = SHA256Util.doubleDigest(bytesInput);
        return HexUtil.bytesToHexString(sha256DoubleDigest);
    }
    /**
     * 计算区块的默克尔树根值
     */
    public static String calculateBlockMerkleTreeRoot(Block block) {
        BlockDTO blockDto = Model2DtoTool.block2BlockDTO(block);
        return calculateBlockMerkleTreeRoot(blockDto);
    }
    /**
     * 计算区块的默克尔树根值
     */
    public static String calculateBlockMerkleTreeRoot(BlockDTO blockDto) {
        List<TransactionDTO> transactions = blockDto.getTransactions();
        List<String> transactionHashList = new ArrayList<>();
        if(transactions != null){
            for(TransactionDTO transactionDto : transactions) {
                String transactionHash = TransactionTool.calculateTransactionHash(transactionDto);
                transactionHashList.add(transactionHash);
            }
        }
        return MerkleTreeUtil.calculateMerkleTreeRootReturnHexadecimal(transactionHashList);
    }
    /**
     * 区块新产生的哈希是否存在重复
     */
    public static boolean isExistDuplicateNewHash(Block block) {
        String blockHash = block.getHash();
        List<Transaction> blockTransactions = block.getTransactions();
        //在不同的交易中，新生产的哈希(区块的哈希、交易的哈希、交易输出哈希)不应该被使用两次或是两次以上
        Set<String> hashSet = new HashSet<>();
        if(hashSet.contains(blockHash)){
            return true;
        }else {
            hashSet.add(blockHash);
        }
        for(Transaction transaction : blockTransactions){
            String transactionHash = transaction.getTransactionHash();
            if(hashSet.contains(transactionHash)){
                return true;
            }else {
                hashSet.add(transactionHash);
            }
        }
        return false;
    }
    /**
     * 区块新产生的地址是否存在重复
     */
    public static boolean isExistDuplicateNewAddress(Block block) {
        Set<String> addressSet = new HashSet<>();
        List<Transaction> blockTransactions = block.getTransactions();
        if(blockTransactions != null){
            for(Transaction transaction : blockTransactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for (TransactionOutput output:outputs){
                        String address = output.getAddress();
                        if(addressSet.contains(address)){
                            LogUtil.debug(String.format("区块数据异常，地址[%s]重复。",address));
                            return true;
                        }else {
                            addressSet.add(address);
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * 是否存在重复的交易输入
     */
    public static boolean isExistDuplicateTransactionInput(Block block) {
        Set<String> transactionOutputIdSet = new HashSet<>();
        for(Transaction transaction : block.getTransactions()){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for(TransactionInput transactionInput : inputs) {
                    TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                    String transactionOutputId = unspentTransactionOutput.getTransactionOutputId();
                    if(transactionOutputIdSet.contains(transactionOutputId)){
                        return true;
                    }else {
                        transactionOutputIdSet.add(transactionOutputId);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 校验区块的时间是否合法
     * 区块时间戳一定要比当前时间戳小。因为挖矿是个技术活，默认矿工有能力将自己机器的时间调整正确。所以矿工不可能穿越到未来挖矿。
     * 区块时间戳一定要比前一个区块的时间戳大。
     */
    public static boolean isBlockTimestampLegal(Block previousBlock, Block currentBlock) {
        if(currentBlock.getTimestamp() > TimeUtil.currentTimeMillis()){
            return false;
        }
        if(previousBlock == null){
            return true;
        } else {
            return currentBlock.getTimestamp() > previousBlock.getTimestamp();
        }
    }

    /**
     * 校验区块的前哈希属性是否正确
     */
    public static boolean isBlockPreviousBlockHashLegal(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            return StringUtil.isEquals(GlobalSetting.GenesisBlock.HASH,currentBlock.getPreviousBlockHash());
        } else {
            return StringUtil.isEquals(previousBlock.getHash(),currentBlock.getPreviousBlockHash());
        }
    }

    /**
     * 校验区块的高度属性是否正确
     */
    public static boolean isBlockHeightLegal(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            return LongUtil.isEquals((GlobalSetting.GenesisBlock.HEIGHT +1),currentBlock.getHeight());
        } else {
            return LongUtil.isEquals((previousBlock.getHeight()+1),currentBlock.getHeight());
        }
    }

    /**
     * 获取区块中交易的数量
     */
    public static long getTransactionCount(Block block) {
        List<Transaction> transactions = block.getTransactions();
        return transactions == null?0:transactions.size();
    }

    /**
     * 两个区块是否相等
     * 注意：这里没有严格校验,例如没有校验交易是否完全一样
     */
    public static boolean isBlockEquals(Block block1, Block block2) {
        if(block1 == null && block2 == null){
            return true;
        }
        if(block1 == null || block2 == null){
            return false;
        }
        return LongUtil.isEquals(block1.getTimestamp(), block2.getTimestamp()) &&
                StringUtil.isEquals(block1.getHash(), block2.getHash()) &&
                StringUtil.isEquals(block1.getPreviousBlockHash(), block2.getPreviousBlockHash()) &&
                StringUtil.isEquals(block1.getMerkleTreeRoot(), block2.getMerkleTreeRoot()) &&
                StringUtil.isEquals(block1.getNonce(), block2.getNonce());
    }

    /**
     * 获取矿工奖励
     */
    public static long getMinerIncentiveValue(Block block) {
        return block.getTransactions().get(0).getOutputs().get(0).getValue();
    }

    /**
     * 格式化难度
     * 前置填零，返回[长度为64位][十六进制字符串形式的]难度
     */
    public static String formatDifficulty(String difficulty) {
        //私钥长度是256bit，64位十六进制的字符串数，如果传入的难度长度不够，这里进行前置补充零操作。
        final int length = 64;
        if(difficulty.length()<length){
            difficulty = (String.join("", Collections.nCopies(length-difficulty.length(), "0")))+difficulty;
        }
        return difficulty;
    }

    public static long getTransactionOutputCount(Block block) {
        long transactionOutputCount = 0;
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                transactionOutputCount += TransactionTool.getTransactionOutputCount(transaction);
            }
        }
        return transactionOutputCount;
    }

    /**
     * 区块总交易手续费
     */
    public static long getBlockFee(Block block) {
        long fees = 0;
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(transaction.getTransactionType() == TransactionType.COINBASE){
                    continue;
                }else if(transaction.getTransactionType() == TransactionType.NORMAL){
                    long fee = TransactionTool.getTransactionFee(transaction);
                    fees += fee;
                }else{
                    throw new RuntimeException("不能识别的交易类型");
                }
            }
        }
        return fees;
    }
}
