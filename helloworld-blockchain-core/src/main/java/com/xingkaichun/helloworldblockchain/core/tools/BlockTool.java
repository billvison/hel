package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;
import com.xingkaichun.helloworldblockchain.setting.Setting;
import com.xingkaichun.helloworldblockchain.util.ListUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

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
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return BlockDtoTool.calculateBlockHash(blockDto);
    }

    /**
     * 计算区块的默克尔树根值
     */
    public static String calculateBlockMerkleTreeRoot(Block block) {
        BlockDto blockDto = Model2DtoTool.block2BlockDto(block);
        return BlockDtoTool.calculateBlockMerkleTreeRoot(blockDto);
    }

    /**
     * 区块新产生的哈希是否存在重复
     */
    public static boolean isExistDuplicateNewHash(Block block) {
        List<String> newHashList = new ArrayList<>();
        String blockHash = block.getHash();
        newHashList.add(blockHash);
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction : transactions){
                String transactionHash = transaction.getTransactionHash();
                newHashList.add(transactionHash);
            }
        }
        return ListUtil.isExistDuplicateElement(newHashList);
    }
    /**
     * 区块新产生的地址是否存在重复
     */
    public static boolean isExistDuplicateNewAddress(Block block) {
        List<String> newAddressList = new ArrayList<>();
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction : transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for (TransactionOutput output:outputs){
                        String address = output.getAddress();
                        newAddressList.add(address);
                    }
                }
            }
        }
        return ListUtil.isExistDuplicateElement(newAddressList);
    }
    /**
     * 区块中是否存在重复的[未花费交易输出]
     */
    public static boolean isExistDuplicateUtxo(Block block) {
        List<String> utxoIdList = new ArrayList<>();
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null) {
            for(Transaction transaction : transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput : inputs) {
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        String utxoId = TransactionTool.getTransactionOutputId(unspentTransactionOutput);
                        utxoIdList.add(utxoId);
                    }
                }
            }
        }
        return ListUtil.isExistDuplicateElement(utxoIdList);
    }

    /**
     * 校验区块的前区块哈希
     */
    public static boolean checkPreviousBlockHash(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            return StringUtil.isEquals(Setting.GenesisBlockSetting.HASH,currentBlock.getPreviousBlockHash());
        } else {
            return StringUtil.isEquals(previousBlock.getHash(),currentBlock.getPreviousBlockHash());
        }
    }

    /**
     * 校验区块高度的连贯性
     */
    public static boolean checkBlockHeight(Block previousBlock, Block currentBlock) {
        if(previousBlock == null){
            return LongUtil.isEquals((Setting.GenesisBlockSetting.HEIGHT +1),currentBlock.getHeight());
        } else {
            return LongUtil.isEquals((previousBlock.getHeight()+1),currentBlock.getHeight());
        }
    }

    /**
     * 校验区块的时间
     * 区块时间戳一定要比当前时间戳小。挖矿是个技术活，默认矿工有能力将自己机器的时间调整正确，所以矿工不应该穿越到未来挖矿。
     * 区块时间戳一定要比前一个区块的时间戳大。
     */
    public static boolean checkBlockTimestamp(Block previousBlock, Block currentBlock) {
        if(currentBlock.getTimestamp() > TimeUtil.currentMillisecondTimestamp()){
            return false;
        }
        if(previousBlock == null){
            return true;
        } else {
            return currentBlock.getTimestamp() > previousBlock.getTimestamp();
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
     * 简单的校验两个区块是否相等
     * 注意：这里没有严格校验,例如没有校验区块中的交易是否完全一样
     * ，所以即使这里认为两个区块相等，实际上这两个区块还是有可能不相等的。
     */
    public static boolean simpleCheckBlockEquals(Block block1, Block block2) {
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
     * 获取激励金额
     */
    public static long getIncentiveValue(Block block) {
        return block.getTransactions().get(0).getOutputs().get(0).getValue();
    }

    /**
     * 格式化难度
     * 前置填零，返回[长度为64位][十六进制字符串形式的]难度
     */
    public static String formatDifficulty(String difficulty) {
        //难度长度是256bit，64位十六进制的字符串数，如果传入的难度长度不够，这里进行前置补充零操作。
        return StringUtil.prefixPadding(difficulty,64,"0");
    }

    /**
     * 获取交易输出数量
     */
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
                if(transaction.getTransactionType() == TransactionType.GENESIS){
                    continue;
                }else if(transaction.getTransactionType() == TransactionType.STANDARD){
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
