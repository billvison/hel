package com.xingkaichun.helloworldblockchain.core.listen;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.enums.BlockChainActionEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.EncodeDecode;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.LevelDBUtil;
import org.iq80.leveldb.DB;

import java.util.List;

/**
 *  同步别的节点的区块时，若删除了自己的的区块，被删除区块的交易信息应当再次加入到交易池
 */
public class BlockChainActionListenerForTransactionPool implements BlockChainActionListener{

    //交易池数据库
    private DB transactionPool_DB;

    public BlockChainActionListenerForTransactionPool(DB transactionPool_DB) {
        this.transactionPool_DB = transactionPool_DB;
    }

    @Override
    public void addOrDeleteBlock(List<BlockChainActionData> blockChainActionDataList) {
        if(blockChainActionDataList == null){
            return;
        }
        for(BlockChainActionData blockChainActionData:blockChainActionDataList){
            if(blockChainActionData.getBlockChainActionEnum() == BlockChainActionEnum.DELETE_BLOCK){
                List<Block> deleteBlockList = blockChainActionData.getBlockList();
                if(deleteBlockList == null){
                    return;
                }
                for(Block deleteBlock:deleteBlockList){
                    for(Transaction transaction:deleteBlock.getTransactions()){
                        if(transaction.getTransactionType() != TransactionType.MINER){
                            String key = transaction.getTransactionUUID();
                            byte[] byteTransaction = new byte[0];
                            try {
                                byteTransaction = EncodeDecode.encode(transaction);
                                LevelDBUtil.put(transactionPool_DB,key,byteTransaction);
                            } catch (Exception e) {
                                //跳过异常
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

}
