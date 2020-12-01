package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.primitives.Bytes;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.MerkleTreeUtil;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 区块工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockTool {

    private static final Logger logger = LoggerFactory.getLogger(BlockTool.class);

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
        byte[] bytesTimestamp = ByteUtil.longToBytes8(blockDto.getTimestamp());
        byte[] bytesPreviousBlockHash = HexUtil.hexStringToBytes(blockDto.getPreviousBlockHash());
        byte[] bytesMerkleTreeRoot = HexUtil.hexStringToBytes(calculateBlockMerkleTreeRoot(blockDto));
        byte[] bytesNonce = ByteUtil.longToBytes8(blockDto.getNonce());

        byte[] bytesData = Bytes.concat(ByteUtil.concatLengthBytes(bytesTimestamp),
                ByteUtil.concatLengthBytes(bytesPreviousBlockHash),
                ByteUtil.concatLengthBytes(bytesMerkleTreeRoot),
                ByteUtil.concatLengthBytes(bytesNonce));
        byte[] sha256Digest = SHA256Util.doubleDigest(bytesData);
        return HexUtil.bytesToHexString(sha256Digest);
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
        List<TransactionDTO> transactions = blockDto.getTransactionDtoList();
        List<byte[]> bytesTransactionHashList = new ArrayList<>();
        if(transactions != null){
            for(TransactionDTO transactionDto : transactions) {
                String transactionHash = TransactionTool.calculateTransactionHash(transactionDto);
                byte[] bytesTransactionHash = HexUtil.hexStringToBytes(transactionHash);
                bytesTransactionHashList.add(bytesTransactionHash);
            }
        }
        return HexUtil.bytesToHexString(MerkleTreeUtil.calculateMerkleTreeRoot(bytesTransactionHashList));
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
            return false;
        }else {
            hashSet.add(blockHash);
        }
        for(Transaction transaction : blockTransactions){
            String transactionHash = transaction.getTransactionHash();
            if(hashSet.contains(transactionHash)){
                return false;
            }else {
                hashSet.add(transactionHash);
            }
        }
        return true;
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
                    TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
                    String transactionOutputId = unspendTransactionOutput.getTransactionOutputId();
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
        if(currentBlock.getTimestamp() > System.currentTimeMillis()){
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
     * 校验激励
     */
    public static boolean isIncentiveRight(long targetMinerReward,Block block) {
        //挖矿激励交易有且只有一笔，挖矿激励交易只能是区块的第一笔交易
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            logger.debug("区块数据异常，没有检测到挖矿奖励交易。");
            return false;
        }
        for(int i=0; i<transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            if(i == 0){
                    boolean incentiveRight = TransactionTool.isIncentiveRight(targetMinerReward,transaction);
                    if(!incentiveRight){
                        logger.debug("区块数据异常，挖矿激励交易异常。");
                        return false;
                    }
            }else {
                if(transaction.getTransactionType() == TransactionType.COINBASE){
                    logger.debug("区块数据异常，挖矿激励交易只能是区块的第一笔交易。");
                    return false;
                }
            }
        }
        return true;
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
                LongUtil.isEquals(block1.getNonce(), block2.getNonce());
    }

    /**
     * 获取矿工奖励
     */
    public static long getMinerIncentiveValue(Block block) {
        return block.getTransactions().get(0).getOutputs().get(0).getValue();
    }
}
