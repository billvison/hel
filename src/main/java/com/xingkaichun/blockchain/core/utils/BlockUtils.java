package com.xingkaichun.blockchain.core.utils;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.utils.atomic.CipherUtil;

import java.util.List;

public class BlockUtils {

    /**
     * 计算区块的Hash值
     * @param block 区块
     */
    public static String calculateBlockHash(Block block) {
        return calculateBlockHash(block.getPreviousHash(),block.getHeight(),block.getMerkleRoot(),block.getNonce());
    }
    //TODO 不面向对象
    public static String calculateBlockHash(String previousHash,int height,String merkleRoot,long nonce) {
        return CipherUtil.applySha256(previousHash+height+merkleRoot+nonce);
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
