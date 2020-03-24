package com.xingkaichun.helloworldblockchain.core.utils;

import com.xingkaichun.helloworldblockchain.core.utils.atomic.CipherUtil;
import com.xingkaichun.helloworldblockchain.model.Block;
import com.xingkaichun.helloworldblockchain.model.transaction.Transaction;

import java.util.List;

public class BlockUtils {

    /**
     * 计算区块的Hash值
     * @param block 区块
     */
    public static String calculateBlockHash(Block block) {
        return CipherUtil.applySha256(block.getTimestamp()+block.getPreviousHash()+block.getHeight()+block.getMerkleRoot()+block.getNonce())+ "-" + block.getTimestamp();
    }

    /**
     * 区块中写入的默克尔树根是否正确
     */
    public static boolean isBlockWriteHashRight(Block block){
        String targetHash = calculateBlockHash(block);
        return targetHash.equals(block.getHash());
    }

    //region 默克尔树根
    /**
     * 计算区块的默克尔树根值
     * @param block 区块
     */
    public static String calculateBlockMerkleRoot(Block block) {
        List<Transaction> transactionList = block.getTransactions();
        return MerkleUtils.getMerkleRoot(transactionList);
    }
    /**
     * 区块中写入的默克尔树根是否正确
     */
    public static boolean isBlockWriteMerkleRootRight(Block block){
        String targetMerkleRoot = calculateBlockMerkleRoot(block);
        return targetMerkleRoot.equals(block.getMerkleRoot());
    }
    //endregion
}
