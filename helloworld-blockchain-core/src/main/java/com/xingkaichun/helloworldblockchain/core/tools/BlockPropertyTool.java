package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.List;

/**
 * 区块工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockPropertyTool {

    /**
     * 区块中的某些属性是由其它属性计算得出，区块对象可能是由外部节点同步过来的。
     * 这里对区块对象中写入的属性值进行严格的校验，通过实际的计算一遍属性值与写入值进行比较，如果不同，则说明区块属性值不正确。
     */
    public static boolean checkWriteProperties(Block previousBlock, Block block) {
        //校验写入的可计算得到的值是否与计算得来的一致
        //校验区块中交易的属性是否与计算得来的一致
        if(!checkBlockWriteTransaction(block)){
            LogUtil.debug("区块写入的交易出错。");
            return false;
        }
        //校验区块中写入的默克尔树根是否正确
        if(!checkBlockWriteMerkleTreeRoot(block)){
            LogUtil.debug("区块写入的默克尔树根出错。");
            return false;
        }
        //校验写入的区块前区块哈希
        if(!BlockTool.checkBlockWritePreviousBlockHash(previousBlock,block)){
            LogUtil.debug("区块写入的前区块哈希出错。");
            return false;
        }
        //校验写入的区块高度是否正确
        if(!BlockTool.checkBlockWriteHeight(previousBlock,block)){
            LogUtil.debug("区块写入的区块高度出错。");
            return false;
        }
        //校验区块中写入的区块哈希是否正确
        if(!checkBlockWriteHash(block)){
            LogUtil.debug("区块写入的区块哈希出错。");
            return false;
        }
        return true;
    }

    /**
     * 校验区块中写入的区块哈希是否正确
     */
    public static boolean checkBlockWriteHash(Block block){
        String targetBlockHash = BlockTool.calculateBlockHash(block);
        return StringUtil.isEquals(targetBlockHash,block.getHash());
    }

    /**
     * 校验区块中写入的默克尔树根是否正确
     */
    public static boolean checkBlockWriteMerkleTreeRoot(Block block){
        String targetMerkleRoot = BlockTool.calculateBlockMerkleTreeRoot(block);
        return StringUtil.isEquals(targetMerkleRoot,block.getMerkleTreeRoot());
    }

    /**
     * 校验区块中交易的属性是否与计算得来的一致
     */
    public static boolean checkBlockWriteTransaction(Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            return true;
        }
        for(Transaction transaction:transactions){
            if(!TransactionPropertyTool.checkWriteProperties(transaction)){
                return false;
            }
        }
        return true;
    }
}
