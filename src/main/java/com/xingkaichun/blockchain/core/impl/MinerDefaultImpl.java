package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.*;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.BlockChainSegement;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.MerkleUtils;
import com.xingkaichun.blockchain.core.utils.atomic.*;
import lombok.Data;

import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * 矿工:挖矿、计算挖矿奖励、计算挖矿难度、校验交易数据的合法性、校验区块数据的合法性。
 */
public class MinerDefaultImpl implements Miner {

    //region 属性与构造函数
    //矿工公钥
    private PublicKeyString minerPublicKey;
    private MineDifficulty mineDifficulty;
    private MineAward mineAward;
    private BlockChainDataBase blockChainDataBase ;
    private BlockChainDataBase blockChainDataBaseSlave ;
    private ForMinerSynchronizeNodeDataBase forMinerSynchronizeNodeDataBase;
    //交易池：矿工从交易池里获取挖矿的原材料(交易数据)
    private ForMinerTransactionDataBase forMinerTransactionDataBase;

    public MinerDefaultImpl(BlockChainDataBase blockChainDataBase, BlockChainDataBase blockChainDataBaseSlave, ForMinerSynchronizeNodeDataBase forMinerSynchronizeNodeDataBase, ForMinerTransactionDataBase forMinerTransactionDataBase, MineDifficulty mineDifficulty, MineAward mineAward, PublicKeyString minerPublicKey) {
        this.blockChainDataBase = blockChainDataBase;
        this.blockChainDataBaseSlave =blockChainDataBaseSlave;
        this.forMinerSynchronizeNodeDataBase = forMinerSynchronizeNodeDataBase;
        this.forMinerTransactionDataBase = forMinerTransactionDataBase;
        this.minerPublicKey = minerPublicKey;
        this.mineDifficulty = mineDifficulty;
        this.mineAward = mineAward;
    }
    //endregion

    //挖矿开关:默认打开挖矿的开关
    private boolean mineOption = true;
    //同步其它节点的区块数据:默认同步其它节点区块数据
    private boolean synchronizeBlockChainNodeOption = true;


    //region 挖矿相关:启动挖矿线程、停止挖矿线程、跳过正在挖的矿
    @Override
    public void run() throws Exception {
        WrapperBlockForMining wrapperBlockForMining = null;
        //分时
        while (true){
            if(mineOption){ break; }
            synchronizeBlockChainNode();
            //是否需要重新获取WrapperBlockForMining？区块链的区块有增删，则需要重新获取。
            if(wrapperBlockForMining == null ||
                    (synchronizeBlockChainNodeOption /* TODO && synchronizeBlockChainNode()*/)){
                wrapperBlockForMining = obtainWrapperBlockForMining();
            }
            adjustMasterSlaveBlockChainDataBase();
            miningBlock(wrapperBlockForMining);
            //挖矿成功
            if(wrapperBlockForMining.getMiningSuccess()){
                //将矿放入区块链
                boolean isAddBlockToBlockChainSuccess = blockChainDataBase.addBlock(wrapperBlockForMining.getBlock());
                if(!isAddBlockToBlockChainSuccess){
                    throw new BlockChainCoreException("区块链新增区块失败。");
                }
                //重置
                wrapperBlockForMining = null;
                //TODO 异常交易不应该彻底丢掉。
                // 例如B交易依赖A交易，但是本区块链并没有成功同步到A交易，因此而判定B交易就是非法的，而将其丢弃。
                // 应当有一个策略，处理这种情形。
                forMinerTransactionDataBase.deleteTransactionList(wrapperBlockForMining.getExceptionTransactionList());
                forMinerTransactionDataBase.deleteTransactionList(wrapperBlockForMining.getTransactionListForMinerBlock());
            }
        }
    }

    /**
     * 调整master slave
     * 第一步：区块高度高的设为master，低的设置slave。
     * 第二步：slave同步master的区块。
     */
    private void adjustMasterSlaveBlockChainDataBase() throws Exception {
        Block masterTailBlock = blockChainDataBase.findTailBlock() ;
        Block slaveTailBlock = blockChainDataBaseSlave.findTailBlock() ;
        //第一步：区块高度高的设为master，低的设置slave。
        if((masterTailBlock == null && slaveTailBlock != null)
            ||(masterTailBlock != null && slaveTailBlock != null && masterTailBlock.getHeight()<slaveTailBlock.getHeight())){
            BlockChainDataBase blockChainDataBaseTemp = blockChainDataBase;
            blockChainDataBase = blockChainDataBaseSlave;
            blockChainDataBaseSlave = blockChainDataBaseTemp;
        }
        //第二步：slave同步master的区块。
        masterTailBlock = blockChainDataBase.findTailBlock() ;
        if(masterTailBlock != null){
            slaveTailBlock = blockChainDataBaseSlave.findTailBlock() ;
            //删除区块直到尚未分叉位置停止
            while(true){
                if(slaveTailBlock == null){
                    break;
                }
                if(isBlockEqual(masterTailBlock,slaveTailBlock)){
                    break;
                }
                blockChainDataBaseSlave.removeTailBlock();
                slaveTailBlock = blockChainDataBaseSlave.findTailBlock() ;
            }
            if(slaveTailBlock == null){
                Block block = blockChainDataBase.findBlockByBlockHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
                blockChainDataBaseSlave.addBlock(block);
            }
            int masterTailBlockHeight = blockChainDataBase.findTailBlock().getHeight() ;
            int slaveTailBlockHeight = blockChainDataBaseSlave.findTailBlock().getHeight() ;
            while(true){
                if(slaveTailBlockHeight >= masterTailBlockHeight){
                    break;
                }
                slaveTailBlockHeight++;
                Block currentBlock = blockChainDataBase.findBlockByBlockHeight(slaveTailBlockHeight) ;
                blockChainDataBaseSlave.addBlock(currentBlock);
            }
        }
    }

    private boolean isBlockEqual(Block masterTailBlock, Block slaveTailBlock) {
        if(masterTailBlock == null && slaveTailBlock == null){
            return true;
        }
        if(masterTailBlock == null || slaveTailBlock == null){
            return false;
        }
        //不严格校验,这里没有具体校验每一笔交易
        if(EqualsUtils.isEquals(masterTailBlock.getPreviousHash(),slaveTailBlock.getPreviousHash())
                && EqualsUtils.isEquals(masterTailBlock.getHeight(),slaveTailBlock.getHeight())
                && EqualsUtils.isEquals(masterTailBlock.getMerkleRoot(),slaveTailBlock.getMerkleRoot())
                && EqualsUtils.isEquals(masterTailBlock.getNonce(),slaveTailBlock.getNonce())
                && EqualsUtils.isEquals(masterTailBlock.getHash(),slaveTailBlock.getHash())){
            return true;
        }
        return false;
    }

    @Override
    public void stopMining(){
        mineOption = false;
    }

    private WrapperBlockForMining obtainWrapperBlockForMining() throws Exception {
        WrapperBlockForMining wrapperBlockForMining = null;
        List<Transaction> transactionListForMinerBlock = forMinerTransactionDataBase.selectTransactionList(0,10000);
        List<Transaction> exceptionTransactionList = removeExceptionTransaction_PointOfBlockView(transactionListForMinerBlock);
        wrapperBlockForMining.setTransactionListForMinerBlock(transactionListForMinerBlock);
        wrapperBlockForMining.setExceptionTransactionList(exceptionTransactionList);

        Block nonNonceBlock = buildNonNonceBlock(transactionListForMinerBlock);
        String difficulty = mineDifficulty.difficulty(blockChainDataBase, nonNonceBlock);


        wrapperBlockForMining.setBlock(nonNonceBlock);
        wrapperBlockForMining.setTargetMineDificultyString(difficulty);
        wrapperBlockForMining.setStartNonce(0L);
        wrapperBlockForMining.setNextNonce(0L);
        wrapperBlockForMining.setMaxTryMiningTimes(100000L);
        wrapperBlockForMining.setMiningSuccess(false);
        return wrapperBlockForMining;
    }
    public void miningBlock(WrapperBlockForMining wrapperBlockForMining) throws Exception {
        //TODO 这里可以利用多处理器的性能进行计算
        Block block = wrapperBlockForMining.getBlock();

        String targetMineDificultyString = wrapperBlockForMining.getTargetMineDificultyString();
        long startNonce = wrapperBlockForMining.getNextNonce();
        long endNonce = wrapperBlockForMining.getStartNonce() + wrapperBlockForMining.getMaxTryMiningTimes();

        String previousHash = block.getPreviousHash();
        int height = block.getHeight();
        String merkleRoot = block.getMerkleRoot();

        for (long currentNonce=startNonce; currentNonce<=endNonce; currentNonce++) {
            if(mineOption){ break; }
            String actualHash = calculateBlockHash(previousHash,height,merkleRoot,currentNonce);
            if(isHashRight(targetMineDificultyString, actualHash)){
                block.setNonce(currentNonce);
                block.setHash(actualHash);
                wrapperBlockForMining.setMiningSuccess(true);
                return;
            }
        }
        wrapperBlockForMining.setNextNonce(endNonce+1);
    }

    @Override
    public void synchronizeBlockChainNode() throws Exception {
        while (true){
            if(!synchronizeBlockChainNodeOption){
                break;
            }
            adjustMasterSlaveBlockChainDataBase();
            String availableSynchronizeNodeId = forMinerSynchronizeNodeDataBase.getDataTransferFinishFlagNodeId();
            if(availableSynchronizeNodeId == null){
                return;
            }
            synchronizeBlockChainNode(availableSynchronizeNodeId);
        }
    }

    private void synchronizeBlockChainNode(String availableSynchronizeNodeId) throws Exception {
        boolean hasDataTransferFinishFlag = forMinerSynchronizeNodeDataBase.hasDataTransferFinishFlag(availableSynchronizeNodeId);
        if(!hasDataTransferFinishFlag){
            return;
        }
        BlockChainSegement blockChainSegement = forMinerSynchronizeNodeDataBase.getNextBlockChainSegement(availableSynchronizeNodeId);
        List<Block> blockList = blockChainSegement.getBlockList();
        Block block = blockList.get(0);
        reduceBlockChain(blockChainDataBaseSlave,block.getHeight()-1);
        while(true){
            if(blockChainSegement == null){
                break;
            }
            for(Block currentBlock : blockList){
                boolean isBlockApplyToBlockChain = isBlockApplyToBlockChain(blockChainDataBaseSlave,currentBlock);
                if(isBlockApplyToBlockChain){
                    blockChainDataBaseSlave.addBlock(currentBlock);
                }else {
                    return;
                }
            }
            blockChainSegement = forMinerSynchronizeNodeDataBase.getNextBlockChainSegement(availableSynchronizeNodeId);
            blockList = blockChainSegement.getBlockList();
        }
        forMinerSynchronizeNodeDataBase.deleteTransferData(availableSynchronizeNodeId);
        forMinerSynchronizeNodeDataBase.clearDataTransferFinishFlag(availableSynchronizeNodeId);
        adjustMasterSlaveBlockChainDataBase();
    }

    private void reduceBlockChain(BlockChainDataBase blockChainDataBase, int blockHeight) throws Exception {
        Block tailBlock = blockChainDataBase.findTailBlock();
        if(tailBlock == null){
            return;
        }
        int currentBlockHeight = tailBlock.getHeight();
        while(currentBlockHeight > blockHeight){
            blockChainDataBase.removeTailBlock();
            tailBlock = blockChainDataBase.findTailBlock();
            if(tailBlock == null){
                return;
            }
            currentBlockHeight = tailBlock.getHeight();
        }
    }

    public void stopSynchronizeBlockChainNode(){
        synchronizeBlockChainNodeOption = false;
    }

    /**
     * 为了辅助挖矿而创造的类
     * 类里包含了一个需要挖矿的区块变量和一些辅助挖矿的变量。
     */
    @Data
    public static class WrapperBlockForMining {
        private Block block;
        //挖矿目标
        private String targetMineDificultyString;
        //挖矿的Nonce起点
        private long startNonce;
        //下一个待验证的Nonce
        private long nextNonce;
        //挖矿的最大尝试次数
        private long maxTryMiningTimes;
        //是否挖矿成功
        private Boolean miningSuccess;

        private List<Transaction> transactionListForMinerBlock;
        private List<Transaction> exceptionTransactionList;
    }
    //endregion


    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在区块的角度校验交易】
     * @param packingTransactionList
     * @throws Exception
     */
    public List<Transaction> removeExceptionTransaction_PointOfBlockView(List<Transaction> packingTransactionList) throws Exception{
        List<Transaction> exceptionTransactionList = new ArrayList<>();
        //区块中允许没有交易
        if(packingTransactionList==null || packingTransactionList.size()==0){
            return exceptionTransactionList;
        }
        List<Transaction> exceptionTransactionList_PointOfTransactionView = removeExceptionTransaction_PointOfTransactionView(packingTransactionList);
        if(exceptionTransactionList_PointOfTransactionView != null){
            exceptionTransactionList.addAll(exceptionTransactionList_PointOfTransactionView);
        }

        //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
        Set<String> transactionOutputUUIDSet = new HashSet<>();
        Iterator<Transaction> iterator = packingTransactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            ArrayList<TransactionInput> inputs = tx.getInputs();
            boolean multiUseOneUTXO = false;
            for(TransactionInput input:inputs){
                String transactionOutputUUID = input.getUnspendTransactionOutput().getTransactionOutputUUID();
                if(transactionOutputUUIDSet.contains(transactionOutputUUID)){
                    multiUseOneUTXO = true;
                }
                transactionOutputUUIDSet.add(transactionOutputUUID);
            }
            if(multiUseOneUTXO){
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
    private List<Transaction>  removeExceptionTransaction_PointOfTransactionView(List<Transaction> transactionList) throws Exception{
        List<Transaction> exceptionTransactionList = new ArrayList<>();
        if(transactionList==null||transactionList.size()==0){
            return exceptionTransactionList;
        }
        Iterator<Transaction> iterator = transactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            boolean checkPass = checkUnBlockChainTransaction(null,tx);
            if(!checkPass){
                iterator.remove();
                exceptionTransactionList.add(tx);
                System.out.println("交易校验失败：丢弃交易。");
            }
        }
        return exceptionTransactionList;
    }




    @Override
    public boolean isBlockApplyToBlockChain(BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        if(block==null){
            throw new BlockChainCoreException("区块校验失败：区块不能为null。");
        }
        //校验区块的连贯性
        Block tailBlock = blockChainDataBase.findTailBlock();
        if(tailBlock == null){
            //校验区块Previous Hash
            if(!BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH.equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度
            if(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT != block.getHeight()){
                return false;
            }
        } else {
            //校验区块Hash是否连贯
            if(!tailBlock.getHash().equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度是否连贯
            if((tailBlock.getHeight()+1) != block.getHeight()){
                return false;
            }
        }
        //校验挖矿[区块本身的数据]是否正确
        boolean minerSuccess = isBlockHashRight(block);
        if(!minerSuccess){
            return false;
        }
        //区块角度检测区块的数据的安全性
        //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
        Set<String> transactionOutputUUIDSet = new HashSet<>();
        //校验即将产生UTXO UUID的唯一性
        Set<String> unspendTransactionOutputUUIDSet = new HashSet<>();
        //挖矿交易笔数，一个区块有且只能有一笔挖矿奖励交易
        int minerTransactionNumber = 0;
        for(Transaction tx : block.getTransactions()){
            String transactionUUID = tx.getTransactionUUID();
            //region 校验交易ID的唯一性
            //校验交易ID的格式
            //校验交易ID的唯一性:之前的区块没用过这个UUID
            //校验交易ID的唯一性:本次校验的区块没有使用这个UUID两次或两次以上
            if(!isUuidAvailableThenAddToSetIfSetNotContainUuid(blockChainDataBase,unspendTransactionOutputUUIDSet,transactionUUID)){
                return false;
            }
            //endregion
            if(tx.getTransactionType() == TransactionType.MINER){
                minerTransactionNumber++;
                //有多个挖矿交易
                if(minerTransactionNumber>1){
                    throw new BlockChainCoreException("区块数据异常，一个区块只能有一笔挖矿奖励。");
                }
                ArrayList<TransactionInput> inputs = tx.getInputs();
                if(inputs!=null && inputs.size()!=0){
                    throw new BlockChainCoreException("交易校验失败：挖矿交易的输入只能为空。不合法的交易。");
                }
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                if(outputs == null){
                    throw new BlockChainCoreException("交易校验失败：挖矿交易的输出不能为空。不合法的交易。");
                }
                if(outputs.size() != 1){
                    throw new BlockChainCoreException("交易校验失败：挖矿交易的输出有且只能有一笔。不合法的交易。");
                }
                TransactionOutput transactionOutput = tx.getOutputs().get(0);
                String unspendTransactionOutputUUID = transactionOutput.getTransactionOutputUUID();
                if(!isUuidAvailableThenAddToSetIfSetNotContainUuid(blockChainDataBase,unspendTransactionOutputUUIDSet,unspendTransactionOutputUUID)){
                    return false;
                }
            } else if(tx.getTransactionType() == TransactionType.NORMAL){
                ArrayList<TransactionInput> inputs = tx.getInputs();
                for(TransactionInput input:inputs){
                    String transactionOutputUUID = input.getUnspendTransactionOutput().getTransactionOutputUUID();
                    //同一个UTXO被多次使用
                    if(transactionOutputUUIDSet.contains(transactionOutputUUID)){
                        throw new BlockChainCoreException("区块数据异常，同一个UTXO在一个区块中多次使用。");
                    }
                    transactionOutputUUIDSet.add(transactionOutputUUID);
                }
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                for(TransactionOutput transactionOutput:outputs){
                    String unspendTransactionOutputUUID = transactionOutput.getTransactionOutputUUID();
                    if(!isUuidAvailableThenAddToSetIfSetNotContainUuid(blockChainDataBase,unspendTransactionOutputUUIDSet,unspendTransactionOutputUUID)){
                        return false;
                    }
                }
            } else {
                throw new BlockChainCoreException("区块数据异常，不能识别的交易类型。");
            }
            boolean check = checkUnBlockChainTransaction(block,tx);
            if(!check){
                throw new BlockChainCoreException("区块数据异常，交易异常。");
            }
        }
        if(minerTransactionNumber == 0){
            throw new BlockChainCoreException("区块数据异常，没有检测到挖矿奖励交易。");
        }
        return true;
    }

    /**
     * UUID格式正确，UUID没有在区块链中被使用，uuidSet包含这个uuid，则返回false，否则，将这个uuid放入uuidSet
     * @param blockChainCore
     * @param uuidSet
     * @param uuid
     * @return
     */
    private boolean isUuidAvailableThenAddToSetIfSetNotContainUuid(BlockChainDataBase blockChainCore, Set<String> uuidSet, String uuid) {
        if(!UuidUtil.isUuidFormatRight(uuid)){
            return false;
        }
        if(blockChainCore.isUuidExist(uuid)){
//            throw new BlockChainCoreException("区块数据异常，UUID在区块链中已经被使用了。");
            return false;
        }
        if(uuidSet.contains(uuid)){
//            throw new BlockChainCoreException("区块数据异常，即将产生的UTXO UUID在区块中使用了两次或者两次以上。");
            return false;
        } else {
            uuidSet.add(uuid);
        }
        return true;
    }


    /**
     * 校验(未打包进区块链的)交易的合法性
     * 奖励交易校验需要传入block参数
     */
    public boolean checkUnBlockChainTransaction(Block block, Transaction transaction) throws Exception{
        if(transaction.getTransactionType() == TransactionType.MINER){
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            if(inputs!=null && inputs.size()!=0){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输入只能为空。不合法的交易。");
            }
            ArrayList<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出不能为空。不合法的交易。");
            }
            if(outputs.size() != 1){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出有且只能有一笔。不合法的交易。");
            }
            if(!isBlockMineAwardRight(block)){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出金额不正确。不合法的交易。");
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            if(inputs==null || inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输入不能为空。不合法的交易。");
            }
            for(TransactionInput i : inputs) {
                if(i.getUnspendTransactionOutput() == null){
                    throw new BlockChainCoreException("交易校验失败：交易的输入UTXO不能为空。不合法的交易。");
                }
                if(blockChainDataBase.findUtxoByUtxoUuid(i.getUnspendTransactionOutput().getTransactionOutputUUID())!=null){
                    throw new BlockChainCoreException("交易校验失败：交易的输入不是UTXO。不合法的交易。");
                }
            }
            if(inputs==null || inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输出不能为空。不合法的交易。");
            }
            //存放交易用过的UTXO
            Set<String> input_UTXO_Ids = new HashSet<>();
            for(TransactionInput i : inputs) {
                String utxoId = i.getUnspendTransactionOutput().getTransactionOutputUUID();
                //校验 同一张钱不能使用两次
                if(input_UTXO_Ids.contains(utxoId)){
                    throw new BlockChainCoreException("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
                }
                input_UTXO_Ids.add(utxoId);
            }
            ArrayList<TransactionOutput> outputs = transaction.getOutputs();
            for(TransactionOutput o : outputs) {
                if(o.getValue().compareTo(new BigDecimal("0"))<=0){
                    throw new BlockChainCoreException("交易校验失败：交易的输出<=0。不合法的交易。");
                }
            }
            BigDecimal inputsValue = TransactionUtil.getInputsValue(transaction);
            BigDecimal outputsValue = TransactionUtil.getOutputsValue(transaction);
            if(inputsValue.compareTo(outputsValue) < 0) {
                throw new BlockChainCoreException("交易校验失败：交易的输入少于交易的输出。不合法的交易。");
            }
            //校验 付款方是同一个用户[公钥] 用户花的钱是自己的钱
            if(!TransactionUtil.isOnlyOneSender(transaction)){
                throw new BlockChainCoreException("交易校验失败：交易的付款方有多个。不合法的交易。");
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

    /*@Override
    public boolean isBlockListApplyToBlockChain(BlockChainSegement blockChainSegement) throws Exception {
        List<Block> changeDeleteBlockList = new ArrayList<>();
        List<Block> changeAddBlockList = new ArrayList<>();
        try {
            if(blockChainSegement == null){
                return false;
            }
            List<Block> blockList = blockChainSegement.getBlockList();
            //被检测区块不允许是空
            if(blockList==null || blockList.size()==0){
                return false;
            }
            Block headBlock = blockList.get(0);
            int headBlockHeight = headBlock.getHeight();

            Block blockchainTailBlock = blockChainDataBase.findTailBlock();
            if(blockchainTailBlock == null){
                if(headBlockHeight != BlockChainCoreConstants.FIRST_BLOCK_HEIGHT){
                    return false;
                }
            }else{
                int blockchainTailBlockHeight = blockchainTailBlock.getHeight();
                if(headBlockHeight < 1){
                    return false;
                }
                if(blockchainTailBlockHeight+1 < headBlockHeight){
                    return false;
                }
                while (blockchainTailBlock.getHeight() >= headBlockHeight){
                    Block removeTailBlock = blockChainDataBase.removeTailBlock();
                    changeDeleteBlockList.add(removeTailBlock);
                    blockchainTailBlock = blockChainDataBase.findTailBlock();
                }
            }

            //防止jvm崩溃，区块丢失
            //TODO 没有异常，直接删除。forMinerSynchronizeNodeDataBase.deleteTransferData(getLocalNodeId());
            BlockChainSegement bcs = new BlockChainSegement();
            bcs.setBlockList(changeDeleteBlockList);
            forMinerSynchronizeNodeDataBase.deleteTransferData(getLocalNodeId());
            forMinerSynchronizeNodeDataBase.addBlockChainSegement(getLocalNodeId(),bcs);
            forMinerSynchronizeNodeDataBase.addDataTransferFinishFlag(getLocalNodeId());

            for(Block block:blockList){
                boolean isBlockApplyToBlockChain = blockChainDataBase.addBlock(block);
                if(isBlockApplyToBlockChain){
                    changeAddBlockList.add(block);
                }else{
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //区块链的区块不应该增加或是减少。
            if(changeAddBlockList.size() != 0){
                int firstAddBlockHeight = changeAddBlockList.get(0).getHeight();
                Block tailBlock = blockChainDataBase.findTailBlock();
                while (firstAddBlockHeight <= tailBlock.getHeight()){
                    blockChainDataBase.removeTailBlock();
                    tailBlock = blockChainDataBase.findTailBlock();
                    if(tailBlock == null){
                        break;
                    }
                }
            }
            if(changeDeleteBlockList.size()!= 0){
                for (int i=changeDeleteBlockList.size()-1; i>=0; i--){
                    blockChainDataBase.addBlock(changeAddBlockList.get(i));
                }
            }
        }
        return true;
    }*/


    //region 挖矿奖励相关
    @Override
    public BigDecimal obtainBlockWriteMineAward(Block block) {
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER){
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                TransactionOutput mineAwardTransactionOutput = outputs.get(0);
                return mineAwardTransactionOutput.getValue();
            }
        }
        throw new BlockChainCoreException("区块数据异常：没有包含挖矿奖励数据。");
    }
    @Override
    public boolean isBlockMineAwardRight(Block block){
        //区块中写入的挖矿奖励
        BigDecimal blockWritedMineAward = obtainBlockWriteMineAward(block);
        //目标挖矿奖励
        BigDecimal targetMineAward = mineAward.mineAward(blockChainDataBase,block);
        return targetMineAward.compareTo(blockWritedMineAward) != 0 ;
    }
    @Override
    public Transaction buildMineAwardTransaction(Block block) {
        Transaction transaction = new Transaction();
        transaction.setTransactionUUID(String.valueOf(UUID.randomUUID()));
        transaction.setTransactionType(TransactionType.MINER);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        BigDecimal award = mineAward.mineAward(blockChainDataBase,block);

        TransactionOutput output = new TransactionOutput();
        output.setTransactionOutputUUID(String.valueOf(UUID.randomUUID()));
        output.setReciepient(minerPublicKey);
        output.setValue(award);

        outputs.add(output);
        transaction.setOutputs(outputs);

        return transaction;
    }
    //endregion


    //region 挖矿Hash相关
    /**
     * Hash满足挖矿难度的要求吗？
     * @param targetDificulty 目标挖矿难度
     * @param hash 需要校验的Hash
     */
    public boolean isHashRight(String targetDificulty,String hash){
        return hash.startsWith(targetDificulty);
    }
    //endregion

    //region 构建区块、计算区块hash、校验区块Nonce
    /**
     * 构建缺少nonce(代表尚未被挖矿)的区块
     */
    public Block buildNonNonceBlock(List<Transaction> packingTransactionList) throws Exception {
        Block tailBlock = blockChainDataBase.findTailBlock();
        Block nonNonceBlock = new Block();
        if(tailBlock == null){
            nonNonceBlock.setHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            nonNonceBlock.setPreviousHash(BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH);
            nonNonceBlock.setTransactions(packingTransactionList);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight()+1);
            nonNonceBlock.setPreviousHash(tailBlock.getHash());
            nonNonceBlock.setTransactions(packingTransactionList);
        }
        Transaction mineAwardTransaction =  buildMineAwardTransaction(nonNonceBlock);
        //将奖励交易加入待打包列表
        packingTransactionList.add(mineAwardTransaction);

        String merkleRoot = calculateBlockMerkleRoot(nonNonceBlock);
        nonNonceBlock.setMerkleRoot(merkleRoot);
        return nonNonceBlock;
    }
    @Override
    public String calculateBlockHash(Block block) {
        return calculateBlockHash(block.getPreviousHash(),block.getHeight(),block.getMerkleRoot(),block.getNonce());
    }
    public String calculateBlockHash(String previousHash,int height,String merkleRoot,long nonce) {
        return CipherUtil.applySha256(previousHash+height+merkleRoot+nonce);
    }
    @Override
    public String calculateBlockMerkleRoot(Block block) {
        List<Transaction> transactionList = block.getTransactions();
        return MerkleUtils.getMerkleRoot(transactionList);
    }
    @Override
    public boolean isBlockMerkleRootRight(Block block){
        String targetMerkleRoot = calculateBlockMerkleRoot(block);
        return targetMerkleRoot.equals(block.getMerkleRoot());
    }
    @Override
    public boolean isBlockHashRight(Block block){
        //校验区块写入的MerkleRoot是否正确
        if(!isBlockMerkleRootRight(block)){
            return false;
        }
        //校验区块写入的挖矿是否正确
        String hash = calculateBlockHash(block);
        if(!hash.equals(block.getHash())){
            return false;
        }
        //校验挖矿是否正确
        String difficulty = mineDifficulty.difficulty(blockChainDataBase,block);
        return isHashRight(difficulty,hash);
    }
    //endregion

    private String getLocalNodeId(){
        return this.getClass().getName();
    }
}
