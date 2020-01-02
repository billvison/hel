package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.*;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.MerkleUtils;
import com.xingkaichun.blockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.blockchain.core.utils.atomic.CipherUtil;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;
import com.xingkaichun.blockchain.core.utils.atomic.UuidUtil;

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
    //交易池：矿工从交易池里获取挖矿的原材料(交易数据)
    private ForMinerTransactionDataBase forMinerTransactionDataBase;

    public MinerDefaultImpl(BlockChainDataBase blockChainDataBase, ForMinerTransactionDataBase forMinerTransactionDataBase, MineDifficulty mineDifficulty, MineAward mineAward, PublicKeyString minerPublicKey) {
        this.blockChainDataBase = blockChainDataBase;
        this.forMinerTransactionDataBase = forMinerTransactionDataBase;
        this.minerPublicKey = minerPublicKey;
        this.mineDifficulty = mineDifficulty;
        this.mineAward = mineAward;
    }
    //endregion



    //region 挖矿相关:启动挖矿线程、停止挖矿线程、跳过正在挖的矿
    /**
     * 停止当前区块的挖矿，可能这个区块已经被挖出来了
     */
    public volatile Boolean stopCurrentBlockMining = false;
    public boolean stopCurrentBlockMining(){
        synchronized (stopCurrentBlockMining){
            if(!stopCurrentBlockMining){
                stopCurrentBlockMining = true;
            }
        }
        return true;
    }

    /**
     * 挖矿
     */
    public Block miningBlock() throws Exception {
        //TODO 清洗数据 将被丢弃的数据从数据库中删除
        List<Transaction> transactionListForMinerBlock = forMinerTransactionDataBase.getTransactionList();
        dropPackingTransactionException_PointOfView_Block(transactionListForMinerBlock);
        while (true){
            Block mineBlock = miningNextBlock(transactionListForMinerBlock);
            if(mineBlock != null){
                blockChainDataBase.addBlock(mineBlock);
            }
            Thread.sleep(1*1000);
        }
    }


    /**
     * 挖矿
     */
    public Block miningNextBlock(List<Transaction> transactionListForMinerBlock) throws Exception {
        //创建打包区块
        Block packingBlock = buildNonNonceBlock(transactionListForMinerBlock);
        int difficulty = mineDifficulty.difficulty(blockChainDataBase, packingBlock);
        packingBlock.setHash(calculateBlockHash(packingBlock));
        String targetMineDificultyString = getTargetMineDificultyString(difficulty);
        boolean miningBlockSuccess = false;
        while (!(miningBlockSuccess=isHashDifficultyRight(targetMineDificultyString, getActualMineDificultyString(packingBlock.getHash(),difficulty)))) {
            //中断挖矿
            synchronized (stopCurrentBlockMining){
                if(stopCurrentBlockMining){
                    stopCurrentBlockMining = false;
                    return null;
                }
            }
            packingBlock.setNonce((packingBlock.getNonce()+1));
            packingBlock.setHash(calculateBlockHash(packingBlock));
        }
        return packingBlock;
    }

    //endregion

    public void dropPackingTransactionException_PointOfView_Block(List<Transaction> packingTransactionList) throws Exception{
        //区块中允许没有交易
        if(packingTransactionList==null || packingTransactionList.size()==0){
            return ;
        }
        dropExceptionPackingTransaction_PointOfView_Transaction(packingTransactionList);
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
                System.out.println("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
            }
        }
    }

    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在单笔交易的角度校验交易】
     */
    private void dropExceptionPackingTransaction_PointOfView_Transaction(List<Transaction> transactionList) throws Exception{
        if(transactionList==null||transactionList.size()==0){
            return;
        }
        Iterator<Transaction> iterator = transactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            try {
                boolean checkPass = checkUnBlockChainTransaction(null,tx);
                if(!checkPass){
                    iterator.remove();
                    System.out.println("交易校验失败：丢弃交易。");
                }
            } catch (Exception e){
                iterator.remove();
                System.out.println("交易校验失败：丢弃交易。");
            }
        }
    }




    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    public boolean isBlockApplyToBlockChain(Block block) throws Exception {
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
            if(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT != block.getBlockHeight()){
                return false;
            }
        } else {
            //校验区块Hash是否连贯
            if(!tailBlock.getHash().equals(block.getPreviousHash())){
                return false;
            }
            //校验区块高度是否连贯
            if((tailBlock.getBlockHeight()+1) != block.getBlockHeight()){
                return false;
            }
        }
        //校验挖矿[区块本身的数据]是否正确
        boolean minerSuccess = isBlockMinedNonceSuccess(block);
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

    /**
     * 检测一串区块是否可以被应用到区块链上
     * 有两种情况，一串区块可以被应用到区块链:
     * 情况1：需要删除一部分链上的区块，然后链上可以衔接这串区块，且删除的区块数目要小于增加的区块的数目
     * 情况2：不需要删除链上的区块，链上直接可以衔接这串区块
     */
    public boolean isBlockListApplyToBlockChain(List<Block> blockList) throws Exception {
        boolean success = true;
        List<Block> changeDeleteBlockList = new ArrayList<>();
        List<Block> changeAddBlockList = new ArrayList<>();
        try {
            //被检测区块不允许是空
            if(blockList==null || blockList.size()==0){
                return false;
            }
            Block headBlock = blockList.get(0);
            int headBlockHeight = headBlock.getBlockHeight();

            Block blockchainTailBlock = blockChainDataBase.findTailBlock();
            if(blockchainTailBlock == null){
                if(headBlockHeight != BlockChainCoreConstants.FIRST_BLOCK_HEIGHT){
                    return false;
                }
            }else{
                int blockchainTailBlockHeight = blockchainTailBlock.getBlockHeight();
                if(headBlockHeight < 1){
                    return false;
                }
                if(blockchainTailBlockHeight+1 < headBlockHeight){
                    return false;
                }
                while (blockchainTailBlock.getBlockHeight() >= headBlockHeight){
                    Block removeTailBlock = blockChainDataBase.removeTailBlock();
                    changeDeleteBlockList.add(removeTailBlock);
                    blockchainTailBlock = blockChainDataBase.findTailBlock();
                }
            }

            for(Block block:blockList){
                boolean isBlockApplyToBlockChain = blockChainDataBase.addBlock(block);
                if(isBlockApplyToBlockChain){
                    changeAddBlockList.add(block);
                }else{
                    success = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(!success){
                if(changeAddBlockList.size() != 0){
                    for (int i=changeAddBlockList.size(); i>=0; i--){
                        blockChainDataBase.removeTailBlock();
                    }
                }
                if(changeDeleteBlockList.size()!= 0){
                    for (int i=changeDeleteBlockList.size(); i>=0; i--){
                        blockChainDataBase.addBlock(changeAddBlockList.get(i));
                    }
                }
                return false;
            }
        }
        return true;
    }


    //region 挖矿奖励相关
    /**
     * 获取区块中写入的挖矿奖励
     * @param block 区块
     * @return
     */
    public BigDecimal extractBlockWritedMineAward(Block block) {
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER){
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                TransactionOutput mineAwardTransactionOutput = outputs.get(0);
                return mineAwardTransactionOutput.getValue();
            }
        }
        throw new BlockChainCoreException("区块数据异常：没有包含挖矿奖励数据。");
    }
    /**
     * 区块的挖矿奖励是否正确？
     * @param block 被校验挖矿奖励是否正确的区块
     * @return
     */
    public boolean isBlockMineAwardRight(Block block){
        List<Transaction> packingTransactionList = new ArrayList<>();
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() != TransactionType.MINER){
                packingTransactionList.add(tx);
            }
        }
        //目标挖矿奖励
        BigDecimal targetMineAward = mineAward.mineAward(blockChainDataBase,block.getBlockHeight(),packingTransactionList);
        //区块中写入的挖矿奖励
        BigDecimal blockWritedMineAward = extractBlockWritedMineAward(block);
        return targetMineAward.compareTo(blockWritedMineAward) != 0 ;
    }
    /**
     * 构建挖矿奖励交易
     * @param blockHeight
     * @param packingTransactionList
     */
    public Transaction buildMineAwardTransaction(int blockHeight, List<Transaction> packingTransactionList) {
        Transaction transaction = new Transaction();
        transaction.setTransactionUUID(String.valueOf(UUID.randomUUID()));
        transaction.setTransactionType(TransactionType.MINER);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        BigDecimal award = mineAward.mineAward(blockChainDataBase,blockHeight,packingTransactionList);

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
     * hash的难度是difficulty吗？
     * @param hash hash
     * @param targetDifficulty 目标难度
     * @return
     */
    public boolean isHashDifficultyRight(String hash,int targetDifficulty){
        String targetMineDificultyString = getTargetMineDificultyString(targetDifficulty);
        String actualMineDificultyString = getActualMineDificultyString(hash, targetDifficulty);
        return isHashDifficultyRight(targetMineDificultyString, actualMineDificultyString);
    }
    /**
     * 挖矿难度正确吗？
     * @param targetMineDificultyString 目标的字符串表示的挖矿难度
     * @param actualMineDificultyString 实际的字符串表示的挖矿难度
     * @return
     */
    public boolean isHashDifficultyRight(String targetMineDificultyString,String actualMineDificultyString){
        return targetMineDificultyString.equals(actualMineDificultyString);
    }
    /**
     * 获取实际的字符串表示的挖矿难度
     * @param hash hash
     * @param targetDifficulty 目标挖矿难度
     * @return
     */
    public String getActualMineDificultyString(String hash, int targetDifficulty){
        return hash.substring(0, targetDifficulty);
    }
    /**
     * 计算字符串表示的挖矿难度目标
     * 示例: 难度为5返回"00000"
     * @param targetDifficulty 目标挖矿难度
     */
    public String getTargetMineDificultyString(int targetDifficulty) {
        return new String(new char[targetDifficulty]).replace('\0', '0');
    }
    //endregion

    //region 构建区块、计算区块hash、校验区块Nonce
    /**
     * 构建缺少nonce(代表尚未被挖矿)的区块
     */
    public Block buildNonNonceBlock(List<Transaction> packingTransactionList) throws Exception {
        Block tailBlock = blockChainDataBase.findTailBlock();
        int blockHeight = tailBlock==null ? BlockChainCoreConstants.FIRST_BLOCK_HEIGHT : tailBlock.getBlockHeight()+1;
        Transaction mineAwardTransaction =  buildMineAwardTransaction(blockHeight,packingTransactionList);
        //将奖励交易加入待打包列表
        packingTransactionList.add(mineAwardTransaction);
        Block nonNonceBlock = new Block();
        if(tailBlock == null){
            nonNonceBlock.setBlockHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            nonNonceBlock.setPreviousHash(BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH);
            nonNonceBlock.setTransactions(packingTransactionList);
        } else {
            nonNonceBlock.setBlockHeight(tailBlock.getBlockHeight()+1);
            nonNonceBlock.setPreviousHash(tailBlock.getHash());
            nonNonceBlock.setTransactions(packingTransactionList);
        }
        String merkleRoot = calculateBlockMerkleRoot(nonNonceBlock);
        nonNonceBlock.setMerkleRoot(merkleRoot);
        return nonNonceBlock;
    }
    /**
     * 计算区块的Hash值
     * @param block 区块
     */
    public String calculateBlockHash(Block block) {
        String merkleRoot = calculateBlockMerkleRoot(block);
        return CipherUtil.applySha256(block.getBlockHeight() + block.getNonce() + block.getPreviousHash() + merkleRoot);
    }
    /**
     * 计算区块的默克尔树根值
     * @param block 区块
     */
    public String calculateBlockMerkleRoot(Block block) {
        List<Transaction> transactionList = block.getTransactions();
        return MerkleUtils.getMerkleRoot(transactionList);
    }
    /**
     * 判断Block的挖矿的成果Nonce是否正确
     */
    public boolean isBlockMinedNonceSuccess(Block block){
        //校验区块写入的MerkleRoot是否正确
        String merkleRoot = calculateBlockMerkleRoot(block);
        if(!merkleRoot.equals(block.getMerkleRoot())){
            return false;
        }
        //校验区块写入的挖矿是否正确
        String hash = calculateBlockHash(block);
        if(!hash.equals(block.getHash())){
            return false;
        }
        int difficulty = mineDifficulty.difficulty(blockChainDataBase,block);
        return isHashDifficultyRight(hash, difficulty);
    }
    //endregion

}
