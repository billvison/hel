package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.MerkleTreeUtil;
import com.xingkaichun.helloworldblockchain.crypto.Sha256Util;
import com.xingkaichun.helloworldblockchain.netcore.dto.BlockDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockDtoTool {

    /**
     * 计算区块的Hash值
     */
    public static String calculateBlockHash(BlockDto blockDto) {
        byte[] bytesTimestamp = ByteUtil.long8ToByte8(blockDto.getTimestamp());
        byte[] bytesPreviousBlockHash = HexUtil.hexStringToBytes(blockDto.getPreviousHash());
        byte[] bytesMerkleTreeRoot = HexUtil.hexStringToBytes(calculateBlockMerkleTreeRoot(blockDto));
        byte[] bytesNonce = HexUtil.hexStringToBytes(blockDto.getNonce());

        byte[] bytesBlockHeader = ByteUtil.concatenate4(bytesTimestamp,bytesPreviousBlockHash,bytesMerkleTreeRoot,bytesNonce);
        byte[] bytesBlockHash = Sha256Util.doubleDigest(bytesBlockHeader);
        return HexUtil.bytesToHexString(bytesBlockHash);
    }

    /**
     * 计算区块的默克尔树根值
     */
    public static String calculateBlockMerkleTreeRoot(BlockDto blockDto) {
        List<TransactionDto> transactions = blockDto.getTransactions();
        List<byte[]> bytesTransactionHashs = new ArrayList<>();
        if(transactions != null){
            for(TransactionDto transactionDto : transactions) {
                String transactionHash = TransactionDtoTool.calculateTransactionHash(transactionDto);
                byte[] bytesTransactionHash = HexUtil.hexStringToBytes(transactionHash);
                bytesTransactionHashs.add(bytesTransactionHash);
            }
        }
        return HexUtil.bytesToHexString(MerkleTreeUtil.calculateMerkleTreeRoot(bytesTransactionHashs));
    }
}
