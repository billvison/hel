package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.*;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.wallet.BuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.wallet.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.util.SystemUtil;

import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainCoreImpl extends BlockchainCore {

    public BlockchainCoreImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDataBase, UnconfirmedTransactionDatabase unconfirmedTransactionDataBase, Wallet wallet, Miner miner) {
        super(coreConfiguration,blockchainDataBase,unconfirmedTransactionDataBase,wallet,miner);
    }

    @Override
    public void start() {
        //启动矿工线程
        new Thread(
                ()->{
                    try {
                        miner.start();
                    } catch (Exception e) {
                        SystemUtil.errorExit("矿工在运行中发生异常，请检查修复异常！",e);
                    }
                }
        ).start();
    }

    @Override
    public long queryBlockchainHeight() {
        return blockchainDataBase.queryBlockchainHeight();
    }


    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        return blockchainDataBase.queryTransactionByTransactionHash(transactionHash);
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        return blockchainDataBase.queryTransactionByTransactionHeight(transactionHeight);
    }

    @Override
    public TransactionOutput queryTransactionOutputByAddress(String address) {
        return blockchainDataBase.queryTransactionOutputByAddress(address);
    }


    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        return blockchainDataBase.queryBlockByBlockHeight(blockHeight);
    }

    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        return blockchainDataBase.queryBlockByBlockHash(blockHash);
    }

    @Override
    public Block queryTailBlock() {
        return blockchainDataBase.queryTailBlock();
    }

    @Override
    public void deleteTailBlock() {
        blockchainDataBase.deleteTailBlock();
    }

    @Override
    public boolean addBlock(Block block) {
        return blockchainDataBase.addBlock(block);
    }


    @Override
    public void deleteBlocks(long blockHeight) {
        blockchainDataBase.deleteBlocks(blockHeight);
    }


    @Override
    public BuildTransactionResponse buildTransactionDto(BuildTransactionRequest request) {
        return wallet.buildTransactionDto(blockchainDataBase,request);
    }

    @Override
    public void submitTransaction(TransactionDto transactionDto) {
        getUnconfirmedTransactionDataBase().insertTransaction(transactionDto);
    }

    @Override
    public List<TransactionDto> queryUnconfirmedTransactions(long from, long size) {
        return getUnconfirmedTransactionDataBase().selectTransactions(from,size);
    }

    @Override
    public TransactionDto queryUnconfirmedTransactionDtoByTransactionHash(String transactionHash) {
        return getUnconfirmedTransactionDataBase().selectTransactionByTransactionHash(transactionHash);
    }
}