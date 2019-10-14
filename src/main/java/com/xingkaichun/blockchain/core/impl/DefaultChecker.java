package com.xingkaichun.blockchain.core.impl;


import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.Checker;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;

import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 区块校验者
 */
public class DefaultChecker implements Checker {

    @Override
    public boolean isBlockApplyToBlockChain(BlockChainCore blockChainCore, Block block) throws Exception {
        if(block==null){
            throw new BlockChainCoreException("区块校验失败：区块不能为null。");
        }
        List<Block> blockList = new ArrayList<>();
        blockList.add(block);
        return isBlockListApplyToBlockChain(blockChainCore,blockList);
    }

    @Override
    public boolean checkUnBlockChainTransaction(BlockChainCore blockChainCore, LightweightBlockChain oldBlocks, LightweightBlockChain newBlocks, Transaction transaction) throws Exception{
        if(transaction.getTransactionType() == TransactionType.MINER){
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输入只能为空。不合法的交易。");
            }
            ArrayList<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出不能为空。不合法的交易。");
            }
            if(outputs.size() != 1){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出有且只能有一笔。不合法的交易。");
            }
            TransactionOutput output = outputs.get(0);
            if(output.getValue().compareTo(new BigDecimal(100))!=0){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出金额不正确。不合法的交易。");
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            if(inputs==null||inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输入不能为空。不合法的交易。");
            }
            for(TransactionInput i : inputs) {
                if(i.getUtxo() ==null){
                    throw new BlockChainCoreException("交易校验失败：交易的输入UTXO不能为空。不合法的交易。");
                }
                if(!isUTXO(blockChainCore,oldBlocks,newBlocks,i.getUtxo().getTransactionOutputUUID())){
                    throw new BlockChainCoreException("交易校验失败：交易的输入不是UTXO。不合法的交易。");
                }
            }
            //存放交易用过的UTXO
            Set<String> input_UTXO_Ids = new HashSet<>();
            for(TransactionInput i : inputs) {
                String utxoId = i.getUtxo().getTransactionOutputUUID();
                //校验 同一张钱不能使用两次
                if(input_UTXO_Ids.contains(utxoId)){
                    throw new BlockChainCoreException("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
                }
                input_UTXO_Ids.add(utxoId);
            }
            ArrayList<TransactionOutput> outputs = transaction.getOutputs();
            if(inputs==null||inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输出不能为空。不合法的交易。");
            }
            for(TransactionOutput o : outputs) {
                if(o.getValue().compareTo(new BigDecimal(0))<=0){
                    throw new BlockChainCoreException("交易校验失败：交易的输出<=0。不合法的交易。");
                }
            }
            BigDecimal inputsValue = TransactionUtil.getInputsValue(transaction);
            BigDecimal outputsValue = TransactionUtil.getOutputsValue(transaction);
            if(inputsValue.compareTo(outputsValue)<0) {
                throw new BlockChainCoreException("交易校验失败：交易的输入少于交易的输出。不合法的交易。");
            }
            //校验 付款方是同一个用户[公钥]
            PublicKeyString sender = TransactionUtil.getSender(transaction);
            for(TransactionInput i : inputs) {
                //校验 用户花的钱是自己的钱
                if(!i.getUtxo().getReciepient().getValue().equals(sender.getValue())){
                    throw new BlockChainCoreException("交易校验失败：交易的付款方有多个。不合法的交易。");
                }
            }
            //校验签名验证
            try{
                if(!TransactionUtil.verifySignature(transaction)) {
                    throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
                }
            }catch (InvalidKeySpecException invalidKeySpecException){
                throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
            }catch (Exception e){
                throw new BlockChainCoreException("交易校验失败：校验交易签名失败。不合法的交易。");
            }
            return true;
        } else {
            throw new BlockChainCoreException("区块数据异常，不能识别的交易类型。");
        }
    }

    @Override
    public boolean isBlockListApplyToBlockChain(BlockChainCore blockChainCore, List<Block> blockList) throws Exception {
        //检测区块是否能衔接上区块链
        if(blockList==null || blockList.size()==0){
            return false;
        }
        Block blockchainTailBlock = blockChainCore.findLastBlockFromBlock();
        Block headPrevBlock = null;
        Block headBlock = blockList.get(0);
        Block tailBlock = blockList.get(blockList.size()-1);
        if(headBlock.getBlockHeight()<1){
            return false;
        }else if(headBlock.getBlockHeight()==1){
        }else if(headBlock.getBlockHeight()>blockchainTailBlock.getBlockHeight()+1){
            return false;
        }else{
            //链的长度:区块链长度只会变长，不会变短
            if(tailBlock.getBlockHeight()<=blockchainTailBlock.getBlockHeight()){
                return false;
            }
            headPrevBlock = blockChainCore.findBlockByBlockHeight(headBlock.getBlockHeight()-1);
            if(!headPrevBlock.getHash().equals(headBlock.getPreviousHash())){
                return false;
            }
        }
        LightweightBlockChain oldBlock = new LightweightBlockChain();
        LightweightBlockChain newBlock = new LightweightBlockChain();
        //需要回滚区块链上的回滚吗？
        if(blockchainTailBlock.getBlockHeight()>=headBlock.getBlockHeight()){
            //回滚
            for(int blockHeight=blockchainTailBlock.getBlockHeight();blockHeight>=headBlock.getBlockHeight();blockHeight--){
                Block currentBlock = blockChainCore.findBlockByBlockHeight(blockHeight);
                fillWriteBatch(oldBlock,currentBlock,true);
            }
        }

        //区块角度检测区块的数据的安全性
        //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
        Set<String> transactionOutputUUIDSet = new HashSet<>();
        for(Block currentBlock:blockList){
            //一个区块只能有一笔挖矿奖励交易
            int minerTransactionTimes = 0;
            for(Transaction tx : currentBlock.getTransactions()){
                if(tx.getTransactionType() == TransactionType.MINER){
                    minerTransactionTimes++;
                    //有多个挖矿交易
                    if(minerTransactionTimes>1){
                        throw new BlockChainCoreException("区块数据异常，一个区块只能有一笔挖矿奖励。");
                    }
                } else if(tx.getTransactionType() == TransactionType.NORMAL){
                    ArrayList<TransactionInput> inputs = tx.getInputs();
                    for(TransactionInput input:inputs){
                        String transactionOutputUUID = input.getUtxo().getTransactionOutputUUID();
                        //同一个UTXO被多次使用
                        if(transactionOutputUUIDSet.contains(transactionOutputUUID)){
                            throw new BlockChainCoreException("区块数据异常，同一个UTXO在一个区块中多次使用。");
                        }
                        transactionOutputUUIDSet.add(transactionOutputUUID);
                    }
                } else {
                    throw new BlockChainCoreException("区块数据异常，不能识别的交易类型。");
                }
                boolean check = checkUnBlockChainTransaction(blockChainCore,oldBlock,newBlock,tx);
                if(!check){
                    throw new BlockChainCoreException("区块数据异常，交易异常。");
                }
            }
            if(minerTransactionTimes == 0){
                throw new BlockChainCoreException("区块数据异常，没有检测到挖矿奖励交易。");
            }
            fillWriteBatch(newBlock,currentBlock,false);
        }
        return true;
    }

    public void fillWriteBatch(LightweightBlockChain blocks, Block block, boolean rollback) throws Exception {
        //UTXO信息
        List<Transaction> packingTransactionList = block.getTransactions();
        if(packingTransactionList!=null){
            for(Transaction transaction:packingTransactionList){
                ArrayList<TransactionInput> inputs = transaction.getInputs();
                if(inputs!=null){
                    for(TransactionInput txInput:inputs){
                        if(rollback){
                            //将用掉的UTXO回滚
                            blocks.getAddUtxoList().add(txInput.getUtxo().getTransactionOutputUUID());
                        } else {
                            blocks.getDeleteUtxoList().add(txInput.getUtxo().getTransactionOutputUUID());
                        }
                    }
                }
                ArrayList<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs!=null){
                    for(TransactionOutput output:outputs){
                        if(rollback){
                            //将新产生的UTXO回滚
                            blocks.getDeleteUtxoList().add(output.getTransactionOutputUUID());
                        } else {
                            blocks.getAddUtxoList().add(output.getTransactionOutputUUID());
                        }
                    }
                }
            }
        }
    }

    private boolean isUTXO(BlockChainCore blockChainCore, LightweightBlockChain rollbackBlocks, LightweightBlockChain newBlocks, String transactionOutputUUID) throws Exception {
        List<String> newBlocks_addUtxoList = newBlocks.getAddUtxoList();
        List<String> newBlocks_deleteUtxoList = newBlocks.getDeleteUtxoList();
        if(newBlocks_deleteUtxoList.contains(transactionOutputUUID)){
            return false;
        }
        if(newBlocks_addUtxoList.contains(transactionOutputUUID)){
            return true;
        }
        List<String> oldBlocks_addUtxoList = rollbackBlocks.getAddUtxoList();
        List<String> oldBlocks_deleteUtxoList = rollbackBlocks.getDeleteUtxoList();
        if(oldBlocks_deleteUtxoList.contains(transactionOutputUUID)){
            return false;
        }
        if(oldBlocks_addUtxoList.contains(transactionOutputUUID)){
            return true;
        }
        boolean isUtxo = blockChainCore.isUTXO(transactionOutputUUID);
        return isUtxo;
    }
}
