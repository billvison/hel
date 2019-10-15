package com.xingkaichun.blockchain.core.miner;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.Checker;
import com.xingkaichun.blockchain.core.impl.LightweightBlockChain;
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

import java.math.BigDecimal;
import java.util.*;

public class Miner {
    private BlockChainCore blockChainCore;
    private PublicKeyString minerPublicKey;
    private MineDifficulty mineDifficulty;
    private TransactionPool transactionPool;
    private MerkleUtils merkleUtils = new MerkleUtils();
    private Checker checker;

    public Miner(TransactionPool transactionPool, MineDifficulty mineDifficulty, BlockChainCore blockChainCore, PublicKeyString minerPublicKey, Checker checker) {
        this.transactionPool = transactionPool;
        this.blockChainCore = blockChainCore;
        this.minerPublicKey = minerPublicKey;
        this.mineDifficulty = mineDifficulty;
        this.checker = checker;
    }

    /**
     * 创建要打包的区块
     */
    public Block createPackingBlock(Block lastBlock, List<Transaction> packingTransactionList) throws Exception {
        //TODO 创建挖矿奖励交易[挖矿奖励只在区块同步时校验]
        Transaction mineAwardTransaction =  createMineAwardTransaction();
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
     */
    public Transaction createMineAwardTransaction() {
        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        Transaction transaction = new Transaction(TransactionType.MINER,null,outputs);
        outputs.add(new TransactionOutput(this.minerPublicKey,new BigDecimal(100),transaction.getTransactionUUID()));
        return transaction;
    }

    /**
     * 挖矿
     */
    public Block mineBlock(Block lastBlock, List<Transaction> packingTransactionList) throws Exception {
        dropPackingTransactionException_PointOfView_Block(blockChainCore,packingTransactionList);

        //创建打包区块
        Block packingBlock = createPackingBlock(lastBlock,packingTransactionList);
        int difficulty = mineDifficulty.difficulty();
        String target = CipherUtil.getDificultyString(difficulty);
        packingBlock.setHash(BlockUtils.calculateHash(packingBlock));
        while (!packingBlock.getHash().substring(0, difficulty).equals(target)) {
            //TODO 中断 其它旷工已经挖到矿了。
            packingBlock.setNonce((packingBlock.getNonce()+1));
            packingBlock.setHash(BlockUtils.calculateHash(packingBlock));
        }
        System.out.println("Block Mined!!! : " + packingBlock.getHash());
        return packingBlock;
    }

    /**
     * 启动挖矿线程
     */
    public void mining() throws Exception {
        new Thread(()->{
            try {
                while (true){
                    Block lastBlock = blockChainCore.findLastBlockFromBlock();
                    Block mineBlock = mineBlock(lastBlock,transactionPool.getForMineTransactionList());
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
                boolean checkPass = checker.checkUnBlockChainTransaction(blockchain,new LightweightBlockChain(),new LightweightBlockChain(),tx);
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
}
