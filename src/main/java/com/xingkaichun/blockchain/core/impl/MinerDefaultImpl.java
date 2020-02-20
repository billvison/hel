package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainDataBase;
import com.xingkaichun.blockchain.core.Miner;
import com.xingkaichun.blockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.blockchain.core.dto.TransactionDTO;
import com.xingkaichun.blockchain.core.dto.TransactionOutputDTO;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.BlockUtils;
import com.xingkaichun.blockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.blockchain.core.utils.atomic.EqualsUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

public class MinerDefaultImpl implements Miner {

    //region 属性与构造函数
    //矿工公钥
    private PublicKeyString minerPublicKey;
    private BlockChainDataBase blockChainDataBase ;
    //交易池：矿工从交易池里获取挖矿的原材料(交易数据)
    private MinerTransactionDtoDataBase minerTransactionDtoDataBase;

    //挖矿开关:默认打开挖矿的开关
    private boolean mineOption = true;


    public MinerDefaultImpl(BlockChainDataBase blockChainDataBase, MinerTransactionDtoDataBase minerTransactionDtoDataBase, PublicKeyString minerPublicKey) {
        this.blockChainDataBase = blockChainDataBase;
        this.minerTransactionDtoDataBase = minerTransactionDtoDataBase;
        this.minerPublicKey = minerPublicKey;
    }
    //endregion



    //region 挖矿相关:启动挖矿线程、停止挖矿线程、跳过正在挖的矿

    private static ThreadLocal<WrapperBlockForMining> wrapperBlockForMiningThreadLocal = new ThreadLocal<>();
    public void mine() throws Exception {
        WrapperBlockForMining wrapperBlockForMining = wrapperBlockForMiningThreadLocal.get();
        //是否需要重新获取WrapperBlockForMining？区块链的区块若有改变，则需要重新获取。
        if(wrapperBlockForMining == null || isWrapperBlockForMiningNeedObtainAgain(wrapperBlockForMining)){
            wrapperBlockForMining = obtainWrapperBlockForMining(blockChainDataBase);
        }
        miningBlock(wrapperBlockForMining);
        //挖矿成功
        if(wrapperBlockForMining.getMiningSuccess()){
            //将矿放入区块链
            boolean isAddBlockToBlockChainSuccess = blockChainDataBase.addBlock(wrapperBlockForMining.getBlock());
            if(!isAddBlockToBlockChainSuccess){
                System.err.println("挖矿成功，但是放入区块链失败。");
                wrapperBlockForMiningThreadLocal.remove();
                return;
            }
            //重置
            minerTransactionDtoDataBase.deleteTransactionDtoList(wrapperBlockForMining.getForMineBlockTransactionDtoList());
            wrapperBlockForMiningThreadLocal.remove();
        }
    }

    private boolean isWrapperBlockForMiningNeedObtainAgain(WrapperBlockForMining wrapperBlockForMining) throws Exception {
        if(wrapperBlockForMining == null){
            return true;
        }
        Block tailBlock = blockChainDataBase.findTailBlock();
        if(tailBlock == null){
            return false;
        }
        Block miningBlock = wrapperBlockForMining.getBlock();
        //TODO 改善型功能 简单校验
        if(EqualsUtils.isEquals(tailBlock.getHash(),miningBlock.getPreviousHash()) &&
                EqualsUtils.isEquals(tailBlock.getHeight(),miningBlock.getHeight()-1)){
            return false;
        }
        return true;
    }

    @Override
    public void pauseMine() throws Exception {
        mineOption = false;
    }

    @Override
    public void resumeMine() throws Exception {
        mineOption = true;
    }

    @Override
    public boolean isActive() throws Exception {
        return mineOption;
    }

    private WrapperBlockForMining obtainWrapperBlockForMining(BlockChainDataBase blockChainDataBase) throws Exception {
        WrapperBlockForMining wrapperBlockForMining = new WrapperBlockForMining();
        List<TransactionDTO> forMineBlockTransactionDtoList = minerTransactionDtoDataBase.selectTransactionDtoList(0,10000);
        wrapperBlockForMining.setForMineBlockTransactionDtoList(forMineBlockTransactionDtoList);
        List<Transaction> forMineBlockTransactionList = classCast(forMineBlockTransactionDtoList);
                //TODO 错误类别处理
        List<Transaction> exceptionTransactionList = removeExceptionTransaction_PointOfBlockView(blockChainDataBase,forMineBlockTransactionList);
        wrapperBlockForMining.setForMineBlockTransactionList(forMineBlockTransactionList);
        wrapperBlockForMining.setExceptionTransactionList(exceptionTransactionList);
        Block nextMineBlock = buildNextMineBlock(blockChainDataBase,forMineBlockTransactionList);

        wrapperBlockForMining.setBlockChainDataBase(blockChainDataBase);
        wrapperBlockForMining.setBlock(nextMineBlock);
        wrapperBlockForMining.setStartNonce(0L);
        wrapperBlockForMining.setNextNonce(0L);
        wrapperBlockForMining.setMaxTryMiningTimes(100000L);
        wrapperBlockForMining.setMiningSuccess(false);
        return wrapperBlockForMining;
    }

    private List<Transaction> classCast(List<TransactionDTO> transactionDtoList) {
        List<Transaction> transactionList = new ArrayList<>();
        if(transactionDtoList != null && transactionDtoList.size()!=0){
            for(TransactionDTO transactionDTO:transactionDtoList){
                try {
                    Transaction transaction = classCast(transactionDTO);
                    transactionList.add(transaction);
                } catch (Exception e){

                }
            }
        }
        return transactionList;
    }

    private Transaction classCast(TransactionDTO transactionDTO) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(transaction.getTimestamp());
        transaction.setTransactionUUID(transactionDTO.getTransactionUUID());
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setSignature(transactionDTO.getSignature());

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        transaction.setInputs(inputs);
        ArrayList<String> dtoInputs = transactionDTO.getInputs();
        if(dtoInputs!=null || dtoInputs.size()!=0){
            for (String dtoInput:dtoInputs){
                TransactionOutput transactionOutput = blockChainDataBase.findUtxoByUtxoUuid(dtoInput);
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setUnspendTransactionOutput(transactionOutput);
                inputs.add(transactionInput);
            }
        }

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        transaction.setOutputs(outputs);
        ArrayList<TransactionOutputDTO> dtoOutputs = transactionDTO.getOutputs();
        if(dtoOutputs!=null || dtoOutputs.size()!=0){
            for(TransactionOutputDTO transactionOutputDTO:dtoOutputs){
                TransactionOutput transactionOutput = classCast(transactionOutputDTO);
                outputs.add(transactionOutput);
            }
        }

        PublicKeyString sender;
        //TODO
        return null;
    }

    private TransactionOutput classCast(TransactionOutputDTO transactionOutputDTO) {
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionOutput.setTransactionOutputUUID(transactionOutputDTO.getTransactionOutputUUID());
        transactionOutput.setReciepient(transactionOutputDTO.getReciepient());
        transactionOutput.setValue(transactionOutputDTO.getValue());
        return transactionOutput;
    }

    public void miningBlock(WrapperBlockForMining wrapperBlockForMining) throws Exception {
        //TODO 改善型功能 这里可以利用多处理器的性能进行计算 还可以进行矿池挖矿
        BlockChainDataBase blockChainDataBase = wrapperBlockForMining.getBlockChainDataBase();
        Block block = wrapperBlockForMining.getBlock();

        long startNonce = wrapperBlockForMining.getNextNonce();
        long endNonce = wrapperBlockForMining.getStartNonce() + wrapperBlockForMining.getMaxTryMiningTimes();

        for (long currentNonce=startNonce; currentNonce<=endNonce; currentNonce++) {
            if(!mineOption){ break; }
            block.setNonce(currentNonce);
            block.setHash(BlockUtils.calculateBlockHash(block));
            if(blockChainDataBase.getConsensus().isReachConsensus(blockChainDataBase,block)){
                wrapperBlockForMining.setMiningSuccess(true);
                break;
            }
        }
        wrapperBlockForMining.setNextNonce(endNonce+1);
    }


    /**
     * 为了辅助挖矿而创造的类
     * 类里包含了一个需要挖矿的区块变量和一些辅助挖矿的变量。
     */
    @Data
    public static class WrapperBlockForMining {
        private Block block;
        //挖矿的Nonce起点
        private long startNonce;
        //下一个待验证的Nonce
        private long nextNonce;
        //挖矿的最大尝试次数
        private long maxTryMiningTimes;
        //是否挖矿成功
        private Boolean miningSuccess;
        private BlockChainDataBase blockChainDataBase;

        private List<Transaction> forMineBlockTransactionList;
        private List<Transaction> exceptionTransactionList;
        private List<TransactionDTO> forMineBlockTransactionDtoList;
    }
    //endregion


    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在区块的角度校验交易】
     * @param packingTransactionList
     * @throws Exception
     */
    public List<Transaction> removeExceptionTransaction_PointOfBlockView(BlockChainDataBase blockChainDataBase,List<Transaction> packingTransactionList) throws Exception{
        List<Transaction> exceptionTransactionList = new ArrayList<>();
        //区块中允许没有交易
        if(packingTransactionList==null || packingTransactionList.size()==0){
            return exceptionTransactionList;
        }
        List<Transaction> exceptionTransactionList_PointOfTransactionView = removeExceptionTransaction_PointOfTransactionView(blockChainDataBase,packingTransactionList);
        if(exceptionTransactionList_PointOfTransactionView != null){
            exceptionTransactionList.addAll(exceptionTransactionList_PointOfTransactionView);
        }

        //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
        //校验UUID的唯一性
        Set<String> uuidSet = new HashSet<>();
        Iterator<Transaction> iterator = packingTransactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            ArrayList<TransactionInput> inputs = tx.getInputs();
            boolean multiTimeUseOneUTXO = false;
            for(TransactionInput input:inputs){
                String unspendTransactionOutputUUID = input.getUnspendTransactionOutput().getTransactionOutputUUID();
                if(!uuidSet.add(unspendTransactionOutputUUID)){
                    multiTimeUseOneUTXO = true;
                    break;
                }
            }
            ArrayList<TransactionOutput> outputs = tx.getOutputs();
            for(TransactionOutput transactionOutput:outputs){
                String transactionOutputUUID = transactionOutput.getTransactionOutputUUID();
                if(!uuidSet.add(transactionOutputUUID)){
                    multiTimeUseOneUTXO = true;
                    break;
                }
            }
            if(multiTimeUseOneUTXO){
                iterator.remove();
                exceptionTransactionList.add(tx);
                System.out.println("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
            }
        }
        return exceptionTransactionList;
    }

    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在单笔交易的角度校验交易】
     */
    private List<Transaction>  removeExceptionTransaction_PointOfTransactionView(BlockChainDataBase blockChainDataBase,List<Transaction> transactionList) throws Exception{
        List<Transaction> exceptionTransactionList = new ArrayList<>();
        if(transactionList==null || transactionList.size()==0){
            return exceptionTransactionList;
        }
        Iterator<Transaction> iterator = transactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            boolean transactionCanAddToNextBlock = blockChainDataBase.isTransactionCanAddToNextBlock(null,tx);
            if(!transactionCanAddToNextBlock){
                iterator.remove();
                exceptionTransactionList.add(tx);
                System.out.println("交易校验失败：丢弃交易。");
            }
        }
        return exceptionTransactionList;
    }

    //region 挖矿奖励相关

    @Override
    public Transaction buildMineAwardTransaction(BlockChainDataBase blockChainDataBase, Block block) {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(System.currentTimeMillis());
        transaction.setTransactionUUID(String.valueOf(UUID.randomUUID()));
        transaction.setTransactionType(TransactionType.MINER);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        BigDecimal award = blockChainDataBase.getIncentive().mineAward(blockChainDataBase,block);

        TransactionOutput output = new TransactionOutput();
        output.setTransactionOutputUUID(String.valueOf(UUID.randomUUID()));
        output.setReciepient(minerPublicKey);
        output.setValue(award);

        outputs.add(output);
        transaction.setOutputs(outputs);
        return transaction;
    }
    //endregion

    //region 构建区块、计算区块hash、校验区块Nonce
    /**
     * 构建挖矿区块
     */
    public Block buildNextMineBlock(BlockChainDataBase blockChainDataBase, List<Transaction> packingTransactionList) throws Exception {
        Block tailBlock = blockChainDataBase.findTailBlock();
        Block nonNonceBlock = new Block();
        if(tailBlock == null){
            nonNonceBlock.setHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            nonNonceBlock.setPreviousHash(BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight()+1);
            nonNonceBlock.setPreviousHash(tailBlock.getHash());
        }
        nonNonceBlock.setTransactions(packingTransactionList);
        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。省去了挖矿时实时更新这个时间的繁琐。
        nonNonceBlock.setTimestamp(System.currentTimeMillis());

        //创建奖励交易，并将奖励加入区块
        Transaction mineAwardTransaction =  buildMineAwardTransaction(blockChainDataBase,nonNonceBlock);
        packingTransactionList.add(mineAwardTransaction);

        String merkleRoot = BlockUtils.calculateBlockMerkleRoot(nonNonceBlock);
        nonNonceBlock.setMerkleRoot(merkleRoot);
        return nonNonceBlock;
    }
    //endregion

    private String getLocalNodeId(){
        return this.getClass().getName();
    }



}
