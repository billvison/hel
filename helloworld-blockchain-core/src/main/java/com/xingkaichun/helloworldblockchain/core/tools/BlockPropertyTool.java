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
    public static boolean isWritePropertiesRight(Block previousBlock, Block block) {
        //校验写入的可计算得到的值是否与计算得来的一致
        //校验交易的属性是否与计算得来的一致
        if(!isBlockTransactionWriteRight(block)){
            LogUtil.debug("区块写入的交易出错。");
            return false;
        }
        //校验写入的MerkleRoot是否与计算得来的一致
        if(!isBlockWriteMerkleTreeRootRight(block)){
            LogUtil.debug("区块写入的默克尔树根出错。");
            return false;
        }
        //校验写入的Hash是否与计算得来的一致
        if(!isBlockWriteHashRight(block)){
            LogUtil.debug("区块写入的区块哈希出错。");
            return false;
        }
        //校验区块前区块哈希
        if(!BlockTool.isBlockPreviousBlockHashLegal(previousBlock,block)){
            LogUtil.debug("区块写入的前区块哈希出错。");
            return false;
        }
        //校验区块高度
        if(!BlockTool.isBlockHeightLegal(previousBlock,block)){
            LogUtil.debug("区块写入的区块高度出错。");
            return false;
        }
        return true;
    }

    /**
     * 区块中写入的默克尔树根是否正确
     */
    public static boolean isBlockWriteHashRight(Block block){
        String targetHash = BlockTool.calculateBlockHash(block);
        return StringUtil.isEquals(targetHash,block.getHash());
    }

    /**
     * 区块中写入的默克尔树根是否正确
     */
    public static boolean isBlockWriteMerkleTreeRootRight(Block block){
        String targetMerkleRoot = BlockTool.calculateBlockMerkleTreeRoot(block);
        return StringUtil.isEquals(targetMerkleRoot,block.getMerkleTreeRoot());
    }

    /**
     * 校验交易的属性是否与计算得来的一致
     */
    public static boolean isBlockTransactionWriteRight(Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            return true;
        }
        for(Transaction transaction:transactions){
            if(!TransactionPropertyTool.isWritePropertiesRight(transaction)){
                return false;
            }
        }
        return true;
    }
}
