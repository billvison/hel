package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Miner;
import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.script.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.NodeTransportDtoTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.core.utils.ThreadUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 默认实现
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class MinerDefaultImpl extends Miner {

    private static final Logger logger = LoggerFactory.getLogger(MinerDefaultImpl.class);

    //region 属性与构造函数
    //挖矿开关:默认打开挖矿的开关
    private boolean mineOption = true;

    public MinerDefaultImpl(BlockChainDataBase blockChainDataBase, MinerTransactionDtoDataBase minerTransactionDtoDataBase, String minerAddress) {
        super(minerAddress,blockChainDataBase,minerTransactionDtoDataBase);
    }
    //endregion


    @Override
    public void start() {
        while(true){
            ThreadUtil.sleep(10);
            if(!mineOption){
                continue;
            }

            Block block = obtainMiningBlock(blockChainDataBase);
            //随机nonce
            long nonce = new Random(Long.MAX_VALUE).nextLong();
            long startTimestamp = System.currentTimeMillis();
            while(true){
                if(!mineOption){
                    break;
                }
                //在挖矿的期间，可能收集到新的交易。每隔一定的时间，重新组装挖矿中的block，组装新的挖矿中的block的时候，可以考虑将新收集到交易放进挖矿中的block。
                if(System.currentTimeMillis()-startTimestamp>1000*10){
                    break;
                }

                block.setNonce(nonce);
                block.setHash(BlockTool.calculateBlockHash(block));
                //挖矿成功
                if(blockChainDataBase.getConsensus().isReachConsensus(blockChainDataBase,block)){
                    logger.info("祝贺您！挖矿成功！！！区块高度:"+block.getHeight()+",区块哈希:"+block.getHash());
                    //将矿放入区块链
                    boolean isAddBlockToBlockChainSuccess = blockChainDataBase.addBlock(block);
                    if(!isAddBlockToBlockChainSuccess){
                        logger.info("挖矿成功，但是放入区块链失败。");
                        continue;
                    }
                    break;
                }
                nonce++;
            }
        }
    }

    @Override
    public void deactive() {
        mineOption = false;
    }

    @Override
    public void active() {
        mineOption = true;
    }

    @Override
    public boolean isActive() {
        return mineOption;
    }

    /**
     * 获取挖矿中的区块对象
     */
    private Block obtainMiningBlock(BlockChainDataBase blockChainDataBase) {
        List<TransactionDTO> forMineBlockTransactionDtoList = minerTransactionDtoDataBase.selectTransactionDtoList(1,10000);
        List<Transaction> forMineBlockTransactionList = new ArrayList<>();
        if(forMineBlockTransactionDtoList != null){
            for(TransactionDTO transactionDTO:forMineBlockTransactionDtoList){
                try {
                    Transaction transaction = NodeTransportDtoTool.classCast(blockChainDataBase,transactionDTO);
                    forMineBlockTransactionList.add(transaction);
                } catch (Exception e) {
                    String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
                    logger.info("类型转换异常,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash,e);
                    minerTransactionDtoDataBase.deleteByTransactionHash(transactionHash);
                }
            }
        }
        removeExceptionTransaction_PointOfBlockView(blockChainDataBase,forMineBlockTransactionList);
        Block nextMineBlock = buildNextMineBlock(blockChainDataBase,forMineBlockTransactionList);
        return nextMineBlock;
    }

    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在区块的角度校验交易】
     */
    public void removeExceptionTransaction_PointOfBlockView(BlockChainDataBase blockChainDataBase,List<Transaction> packingTransactionList) {
        if(packingTransactionList==null || packingTransactionList.size()==0){
            return;
        }
        removeExceptionTransaction_PointOfTransactionView(blockChainDataBase,packingTransactionList);

        Set<String> hashSet = new HashSet<>();
        Iterator<Transaction> iterator = packingTransactionList.iterator();
        while (iterator.hasNext()){
            Transaction transaction = iterator.next();
            List<TransactionInput> inputs = transaction.getInputs();
            boolean isError = false;
            //校验双花：同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
            for(TransactionInput input:inputs){
                String unspendTransactionOutputHash = input.getUnspendTransactionOutput().getTransactionOutputHash();
                if(hashSet.contains(unspendTransactionOutputHash)){
                    isError = true;
                    break;
                }else {
                    hashSet.add(unspendTransactionOutputHash);
                }
            }
            //校验哈希，哈希不能重复使用
            List<TransactionOutput> outputs = transaction.getOutputs();
            for(TransactionOutput transactionOutput:outputs){
                String transactionOutputHash = transactionOutput.getTransactionOutputHash();
                if(hashSet.contains(transactionOutputHash)){
                    isError = true;
                    break;
                }else {
                    hashSet.add(transactionOutputHash);
                }
            }
            if(isError){
                iterator.remove();
                minerTransactionDtoDataBase.deleteByTransactionHash(transaction.getTransactionHash());
                logger.debug("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
            }
        }
    }

    /**
     * 打包处理过程: 将异常的交易丢弃掉【站在单笔交易的角度校验交易】
     */
    private void removeExceptionTransaction_PointOfTransactionView(BlockChainDataBase blockChainDataBase,List<Transaction> transactionList) {
        if(transactionList==null || transactionList.size()==0){
            return;
        }
        Iterator<Transaction> iterator = transactionList.iterator();
        while (iterator.hasNext()){
            Transaction transaction = iterator.next();
            boolean transactionCanAddToNextBlock = blockChainDataBase.isTransactionCanAddToNextBlock(null,transaction);
            if(!transactionCanAddToNextBlock){
                iterator.remove();
                minerTransactionDtoDataBase.deleteByTransactionHash(transaction.getTransactionHash());
                logger.debug("交易校验失败：丢弃交易。交易哈希"+transaction.getTransactionHash());
            }
        }
    }

    @Override
    public Transaction buildMineAwardTransaction(long timestamp, BlockChainDataBase blockChainDataBase, Block block) {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(timestamp);
        transaction.setTransactionType(TransactionType.COINBASE);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        long award = blockChainDataBase.getIncentive().mineAward(block);

        TransactionOutput output = new TransactionOutput();
        output.setTransactionOutputSequence(1);
        output.setTimestamp(timestamp);
        output.setAddress(minerAddress);
        output.setValue(award);
        output.setScriptLock(StackBasedVirtualMachine.createPayToPublicKeyHashOutputScript(minerAddress));
        output.setTransactionOutputHash(TransactionTool.calculateTransactionOutputHash(transaction,output));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transaction));
        return transaction;
    }

    /**
     * 构建挖矿区块
     */
    public Block buildNextMineBlock(BlockChainDataBase blockChainDataBase, List<Transaction> packingTransactionList) {
        long timestamp = System.currentTimeMillis();

        Block tailBlock = blockChainDataBase.queryTailBlock();
        Block nonNonceBlock = new Block();
        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。省去了挖矿时实时更新这个时间的繁琐。
        nonNonceBlock.setTimestamp(timestamp);

        if(tailBlock == null){
            nonNonceBlock.setHeight(GlobalSetting.GenesisBlockConstant.FIRST_BLOCK_HEIGHT);
            nonNonceBlock.setPreviousBlockHash(GlobalSetting.GenesisBlockConstant.FIRST_BLOCK_PREVIOUS_HASH);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight()+1);
            nonNonceBlock.setPreviousBlockHash(tailBlock.getHash());
        }
        nonNonceBlock.setTransactions(packingTransactionList);

        //创建挖矿奖励交易
        Transaction mineAwardTransaction =  buildMineAwardTransaction(timestamp,blockChainDataBase,nonNonceBlock);
        packingTransactionList.add(0,mineAwardTransaction);


        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(nonNonceBlock);
        nonNonceBlock.setMerkleTreeRoot(merkleTreeRoot);
        return nonNonceBlock;
    }
}
