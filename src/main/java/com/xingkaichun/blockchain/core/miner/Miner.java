package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.exception.BlockChainCoreException;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.blockchain.core.model.transaction.TransactionType;
import com.xingkaichun.blockchain.core.utils.BlockUtils;
import com.xingkaichun.blockchain.core.utils.MerkleUtils;
import com.xingkaichun.blockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.blockchain.core.utils.atomic.CipherUtil;
import com.xingkaichun.blockchain.core.utils.atomic.TransactionUtil;

import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class Miner {
    private PublicKeyString minerPublicKey;
    private MineDifficulty mineDifficulty;
    private MineAward mineAward;
    private TransactionPool transactionPool;
    private MerkleUtils merkleUtils = new MerkleUtils();

    public Miner(TransactionPool transactionPool, MineDifficulty mineDifficulty, MineAward mineAward, PublicKeyString minerPublicKey) {
        this.transactionPool = transactionPool;
        this.minerPublicKey = minerPublicKey;
        this.mineDifficulty = mineDifficulty;
        this.mineAward = mineAward;
    }

    /**
     * 创建要打包的区块
     */
    public Block createPackingBlock(BlockChainCore blockChainCore, Block lastBlock, List<Transaction> packingTransactionList) throws Exception {
        int blockHeight = lastBlock==null ? BlockChainCoreConstants.FIRST_BLOCK_HEIGHT : lastBlock.getBlockHeight()+1;
        Transaction mineAwardTransaction =  createMineAwardTransaction(blockChainCore,blockHeight,packingTransactionList);
        //将奖励交易加入待打包列表
        packingTransactionList.add(mineAwardTransaction);
        Block packingBlock = null;
        if(lastBlock==null){
            packingBlock = new Block(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT, BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH, packingTransactionList,merkleUtils.getMerkleRoot(packingTransactionList));
        } else {
            packingBlock = new Block(lastBlock.getBlockHeight()+1, lastBlock.getHash(),packingTransactionList,merkleUtils.getMerkleRoot(packingTransactionList));
        }
        return packingBlock;
    }

    /**
     * 创建挖矿交易
     * @param blockChainCore
     * @param blockHeight
     * @param packingTransactionList
     */
    public Transaction createMineAwardTransaction(BlockChainCore blockChainCore, int blockHeight, List<Transaction> packingTransactionList) {
        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        Transaction transaction = new Transaction(TransactionType.MINER,null,outputs);
        BigDecimal award = mineAward.mineAward(blockChainCore,blockHeight,packingTransactionList);
        outputs.add(new TransactionOutput(this.minerPublicKey,award,transaction.getTransactionUUID()));
        return transaction;
    }

    /**
     * 获取区块中写入的挖矿奖励
     * @param block
     * @return
     */
    public BigDecimal extractMineAward(Block block) {
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() == TransactionType.MINER){
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                TransactionOutput mineAwardTransactionOutput = outputs.get(0);
                return mineAwardTransactionOutput.getValue();
            }
        }
        throw new BlockChainCoreException("区块数据异常：没有包含挖矿奖励数据。");
    }

    public boolean isBlockMineAwardRight(BlockChainCore blockChainCore, Block block){
        List<Transaction> packingTransactionList = new ArrayList<>();
        for(Transaction tx : block.getTransactions()){
            if(tx.getTransactionType() != TransactionType.MINER){
                packingTransactionList.add(tx);
            }
        }
        //计算的挖矿奖励
        BigDecimal mineAward = getMineAward().mineAward(blockChainCore,block.getBlockHeight(),packingTransactionList);
        //区块中写入的挖矿奖励
        BigDecimal mineAwardByPass = extractMineAward(block);
        return mineAward.compareTo(mineAwardByPass) != 0 ;
    }

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
    public Block mineBlock(BlockChainCore blockChainCore, Block lastBlock, List<Transaction> packingTransactionList) throws Exception {
        dropPackingTransactionException_PointOfView_Block(blockChainCore,packingTransactionList);

        //创建打包区块
        Block packingBlock = createPackingBlock(blockChainCore,lastBlock,packingTransactionList);
        int difficulty = mineDifficulty.difficulty(blockChainCore, packingBlock);
        String difficultyString = CipherUtil.getDificultyString(difficulty);
        packingBlock.setHash(BlockUtils.calculateHash(packingBlock));
        while (!isHashSuccess(packingBlock.getHash(),difficulty,difficultyString)) {
            //中断挖矿
            synchronized (stopCurrentBlockMining){
                if(stopCurrentBlockMining){
                    stopCurrentBlockMining = false;
                    break;
                }
            }
            packingBlock.setNonce((packingBlock.getNonce()+1));
            packingBlock.setHash(BlockUtils.calculateHash(packingBlock));
        }
        System.out.println("Block Mined!!! : " + packingBlock.getHash());
        return packingBlock;
    }

    /**
     * 判断Block的挖矿Hash是否正确
     */
    public boolean isMinedBlockSuccess(BlockChainCore blockChainCore, Block block){
        //校验markettree
        String merkleRoot = merkleUtils.getMerkleRoot(block.getTransactions());
        if(!merkleRoot.equals(block.getMerkleRoot())){
            return false;
        }
        //校验挖矿难度是否正确
        String hash = BlockUtils.calculateHash(block);
        if(!hash.equals(block.getHash())){
            return false;
        }
        int difficulty = mineDifficulty.difficulty(blockChainCore,block);
        return isHashSuccess(hash, difficulty);
    }
    public boolean isHashSuccess(String hash,int difficulty){
        String difficultyString = CipherUtil.getDificultyString(difficulty);
        return isHashSuccess(hash,difficulty,difficultyString);
    }
    public boolean isHashSuccess(String hash,int difficulty,String difficultyString){
        return hash.substring(0, difficulty).equals(difficultyString);
    }
    /**
     * 启动挖矿线程
     */
    public void mining(BlockChainCore blockChainCore) throws Exception {
        new Thread(()->{
            try {
                while (true){
                    Block lastBlock = blockChainCore.findLastBlockFromBlock();
                    Block mineBlock = mineBlock(blockChainCore,lastBlock,transactionPool.getTransactionListForMine());
                    if(mineBlock != null){
                        blockChainCore.addBlock(mineBlock);
                    }
                    Thread.sleep(5*60*1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在区块的角度校验交易】
     */
    public void dropPackingTransactionException_PointOfView_Block(BlockChainCore coreBlockChain, List<Transaction> packingTransactionList) throws Exception{
        if(packingTransactionList==null||packingTransactionList.size()==0){
            return;
        }
        dropExceptionPackingTransaction_PointOfView_Transaction(coreBlockChain,packingTransactionList);
        //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
        Set<String> transactionOutputUUIDSet = new HashSet<>();
        Iterator<Transaction> iterator = packingTransactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            ArrayList<TransactionInput> inputs = tx.getInputs();
            boolean multiUseOneUTXO = false;
            for(TransactionInput input:inputs){
                String transactionOutputUUID = input.getUtxo().getTransactionOutputUUID();
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
    private void dropExceptionPackingTransaction_PointOfView_Transaction(BlockChainCore blockchain, List<Transaction> transactionList) throws Exception{
        if(transactionList==null||transactionList.size()==0){
            return;
        }
        Iterator<Transaction> iterator = transactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            try {
                boolean checkPass = checkUnBlockChainTransaction(blockchain,null,tx);
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

    public MineDifficulty getMineDifficulty() {
        return mineDifficulty;
    }

    public MineAward getMineAward() {
        return mineAward;
    }












    /**
     * 检测区块是否可以被应用到区块链上
     * 只有一种情况，区块可以被应用到区块链，即: 区块是区块链上的下一个区块
     */
    public boolean isBlockApplyToBlockChain(BlockChainCore blockChainCore, Block block) throws Exception {
        if(block==null){
            throw new BlockChainCoreException("区块校验失败：区块不能为null。");
        }
        //校验挖矿[区块本身的数据]是否正确
        boolean minerSuccess = blockChainCore.getMiner().isMinedBlockSuccess(blockChainCore,block);
        if(!minerSuccess){
            return false;
        }
        //校验区块的连贯性
        Block tailBlock = blockChainCore.findLastBlockFromBlock();
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
        //区块角度检测区块的数据的安全性
        //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
        Set<String> transactionOutputUUIDSet = new HashSet<>();
        //校验即将产生UTXO UUID的唯一性
        Set<String> unspendTransactionOutputUUIDSet = new HashSet<>();
        //校验交易UUID的唯一性
        Set<String> transactionUUIDSet = new HashSet<>();
        //一个区块只能有一笔挖矿奖励交易
        int minerTransactionTimes = 0;
        for(Transaction tx : block.getTransactions()){
            String transactionUUID = tx.getTransactionUUID();
            //region 校验交易ID的唯一性
            //校验交易ID的唯一性:之前的区块没用过这个UUID
            if(!blockChainCore.isExistTransaction(transactionUUID)){
                throw new BlockChainCoreException("区块数据异常，交易的id在之前的区块中已经被使用了。");
            }
            //校验交易ID的唯一性:本次校验的区块没有使用这个UUID两次或两次以上
            if(transactionUUIDSet.contains(transactionUUID)){
                throw new BlockChainCoreException("区块数据异常，交易的id在本次校验的区块中使用了两次或者两次以上。");
            } else {
                transactionUUIDSet.add(transactionUUID);
            }
            //endregion
            if(tx.getTransactionType() == TransactionType.MINER){
                minerTransactionTimes++;
                //有多个挖矿交易
                if(minerTransactionTimes>1){
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
                if(unspendTransactionOutputUUIDSet.contains(unspendTransactionOutputUUID)){
                    throw new BlockChainCoreException("区块数据异常，即将产生的UTXO UUID在区块中使用了两次或者两次以上。");
                } else {
                    unspendTransactionOutputUUIDSet.add(unspendTransactionOutputUUID);
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
                ArrayList<TransactionOutput> outputs = tx.getOutputs();
                for(TransactionOutput transactionOutput:outputs){
                    String unspendTransactionOutputUUID = transactionOutput.getTransactionOutputUUID();
                    if(unspendTransactionOutputUUIDSet.contains(unspendTransactionOutputUUID)){
                        throw new BlockChainCoreException("区块数据异常，即将产生的UTXO UUID在区块中使用了两次或者两次以上。");
                    } else {
                        unspendTransactionOutputUUIDSet.add(unspendTransactionOutputUUID);
                    }
                }
            } else {
                throw new BlockChainCoreException("区块数据异常，不能识别的交易类型。");
            }
            boolean check = checkUnBlockChainTransaction(blockChainCore,block,tx);
            if(!check){
                throw new BlockChainCoreException("区块数据异常，交易异常。");
            }
        }
        if(minerTransactionTimes == 0){
            throw new BlockChainCoreException("区块数据异常，没有检测到挖矿奖励交易。");
        }
        return true;
    }

    /**
     * 检测一串区块是否可以被应用到区块链上
     * 有两种情况，一串区块可以被应用到区块链:
     * 情况1：需要删除一部分链上的区块，然后链上可以衔接这串区块，且删除的区块数目要小于增加的区块的数目
     * 情况2：不需要删除链上的区块，链上直接可以衔接这串区块
     */
    public boolean checkUnBlockChainTransaction(BlockChainCore blockChainCore, Block block, Transaction transaction) throws Exception{
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
            if(!blockChainCore.getMiner().isBlockMineAwardRight(blockChainCore,block)){
                throw new BlockChainCoreException("交易校验失败：挖矿交易的输出金额不正确。不合法的交易。");
            }
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            ArrayList<TransactionInput> inputs = transaction.getInputs();
            if(inputs==null || inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输入不能为空。不合法的交易。");
            }
            for(TransactionInput i : inputs) {
                if(i.getUtxo() == null){
                    throw new BlockChainCoreException("交易校验失败：交易的输入UTXO不能为空。不合法的交易。");
                }
                if(!blockChainCore.isUTXO(i.getUtxo().getTransactionOutputUUID())){
                    throw new BlockChainCoreException("交易校验失败：交易的输入不是UTXO。不合法的交易。");
                }
            }
            if(inputs==null || inputs.size()==0){
                throw new BlockChainCoreException("交易校验失败：交易的输出不能为空。不合法的交易。");
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
     * 校验(未打包进区块链的)交易的合法性
     * 奖励交易校验需要传入block参数
     */
    public boolean isBlockListApplyToBlockChain(BlockChainCore blockChainCore, List<Block> blockList) throws Exception {
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

            Block blockchainTailBlock = blockChainCore.findLastBlockFromBlock();
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
                    Block removeTailBlock = blockChainCore.removeTailBlock();
                    changeDeleteBlockList.add(removeTailBlock);
                    blockchainTailBlock = blockChainCore.findLastBlockFromBlock();
                }
            }

            for(Block block:blockList){
                boolean isBlockApplyToBlockChain = blockChainCore.addBlock(block);
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
                        blockChainCore.removeTailBlock();
                    }
                }
                if(changeDeleteBlockList.size()!= 0){
                    for (int i=changeDeleteBlockList.size(); i>=0; i--){
                        blockChainCore.addBlock(changeAddBlockList.get(i));
                    }
                }
                return false;
            }
        }
        return true;
    }
}
