package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;

import java.util.List;


/**
 * 区块链
 */
public interface BlockChainDataBase {

    //region 区块增加与删除
    /**
     * 区块链新增区块
     * 注意多线程、数据校验
     */
    boolean addBlock(Block block, boolean checkBlock, boolean notifyBlockChainActionListener) throws Exception ;

    /**
     * 删除区块链的尾巴[最后一个]区块
     */
    Block removeTailBlock(boolean notifyBlockChainActionListener) throws Exception ;

    /**
     * 回滚老的区块，并新增区块
     */
    boolean replaceBlocks(List<Block> addBlockList, boolean checkBlock, boolean notifyBlockChainActionListener) throws Exception ;
    //endregion

    //region 区块链提供的通用方法
    /**
     * 查找区块链上的最后一个区块
     */
    Block findTailBlock() throws Exception ;

    /**
     * 在区块链中根据 UTXO ID 查找UTXO
     * @param transactionOutputUUID UTXO ID
     */
    TransactionOutput findUtxoByUtxoUuid(String transactionOutputUUID) throws Exception ;

    /**
     * 在区块链中根据区块高度查找区块
     * @param blockHeight 区块高度
     */
    Block findBlockByBlockHeight(int blockHeight) throws Exception ;

    /**
     * 在区块链中根据交易ID查找交易
     * @param transactionUUID 交易ID
     */
    Transaction findTransactionByTransactionUuid(String transactionUUID) throws Exception ;

    /**
     * 交易是否已经存在于区块链之中？
     * @param transactionUUID 交易ID
     */
    boolean isTransactionExist(String transactionUUID) throws Exception ;

    /**
     * UUID是否已经存在于区块链之中？
     * @param uuid uuid
     */
    boolean isUuidExist(String uuid) ;
    //endregion

    //region 监听器
    void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener) ;
    void notifyBlockChainActionListener(List<BlockChainActionData> dataList) ;
    List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) ;
    List<BlockChainActionData> createBlockChainActionDataList(List<Block> firstBlockList, BlockChainActionEnum firstBlockChainActionEnum, List<Block> nextBlockList, BlockChainActionEnum nextBlockChainActionEnum) ;
    //endregion
}