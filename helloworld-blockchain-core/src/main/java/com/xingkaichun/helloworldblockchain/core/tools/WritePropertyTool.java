package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.List;

/**
 * 写入属性工具类
 * 区块中的某些属性是由其它属性计算得出，例如区块哈希，由(时间戳、前区块哈希、默克尔树根、随机数)计算生成
 * ，而区块哈希可能(注意：是可能，主要看节点数据交互怎么写了)由其它节点直接传输过来(它可能造假)
 * ，后续业务需要使用区块哈希，是直接使用这个区块哈希
 * ，还是由(时间戳、前区块哈希、默克尔树根、随机数)重新计算生成区块哈希再使用？
 * 这两者由于节点可能作假，可能并不一致，为了避免业务的复杂性
 * ，所以我们直接将所有这种可以由其它属性计算得出的属性，先进行一次校验(属性值与重新计算得出是否一致)
 * ，后续业务就省心了，直接使用属性值就可以了。
 *
 * @author 邢开春 409060350@qq.com
 */
public class WritePropertyTool {

    /**
     * 校验区块中写入的可计算得到的值是否与计算得出一致
     */
    public static boolean checkBlockWriteProperties(Block block) {
        //校验区块中交易的属性是否与计算得出一致
        if(!checkBlockWritePropertyTransaction(block)){
            LogUtil.debug("区块写入的交易出错。");
            return false;
        }
        //校验区块中写入的默克尔树根是否正确
        if(!checkBlockWritePropertyMerkleTreeRoot(block)){
            LogUtil.debug("区块写入的默克尔树根出错。");
            return false;
        }
        //校验区块中写入的区块哈希是否与计算得出一致
        if(!checkBlockWritePropertyBlockHash(block)){
            LogUtil.debug("区块写入的区块哈希出错。");
            return false;
        }
        return true;
    }

    /**
     * 校验区块中写入的区块哈希是否与计算得出一致
     */
    public static boolean checkBlockWritePropertyBlockHash(Block block){
        String targetBlockHash = BlockTool.calculateBlockHash(block);
        return StringUtil.isEquals(targetBlockHash,block.getHash());
    }

    /**
     * 校验区块中写入的默克尔树根是否与计算得出一致
     */
    public static boolean checkBlockWritePropertyMerkleTreeRoot(Block block){
        String targetMerkleRoot = BlockTool.calculateBlockMerkleTreeRoot(block);
        return StringUtil.isEquals(targetMerkleRoot,block.getMerkleTreeRoot());
    }

    /**
     * 校验区块中交易写入的属性是否与计算得出一致
     */
    public static boolean checkBlockWritePropertyTransaction(Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            return true;
        }
        for(Transaction transaction:transactions){
            if(!checkTransactionWriteProperties(transaction)){
                return false;
            }
        }
        return true;
    }



    /**
     * 校验交易写入的属性值是否与计算得出一致
     */
    public static boolean checkTransactionWriteProperties(Transaction transaction) {
        if(!checkTransactionWritePropertyTransactionHash(transaction)){
            return false;
        }
        return true;
    }

    /**
     * 校验交易写入的交易哈希是否与计算得出一致
     */
    public static boolean checkTransactionWritePropertyTransactionHash(Transaction transaction) {
        String targetTransactionHash = TransactionTool.calculateTransactionHash(transaction);
        return StringUtil.isEquals(targetTransactionHash,transaction.getTransactionHash());
    }
}
