package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Miner;
import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.helloworldblockchain.core.utils.BlockUtils;
import com.xingkaichun.helloworldblockchain.core.utils.NodeTransportUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockChainCoreConstants;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockchainUuidUtil;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.ConsensusTarget;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class MinerDefaultImpl extends Miner {

    private Logger logger = LoggerFactory.getLogger(MinerDefaultImpl.class);

    //region 属性与构造函数
    //挖矿开关:默认打开挖矿的开关
    private boolean mineOption = true;

    /**
     * 存储正在挖矿中的区块
     */
    private ThreadLocal<MiningBlock> miningBlockThreadLocal;


    public MinerDefaultImpl(BlockChainDataBase blockChainDataBase, MinerTransactionDtoDataBase minerTransactionDtoDataBase, StringAddress minerStringAddress) {
        this.blockChainDataBase = blockChainDataBase;
        this.minerTransactionDtoDataBase = minerTransactionDtoDataBase;
        this.minerStringAddress = minerStringAddress;

        miningBlockThreadLocal = new ThreadLocal<>();
    }
    //endregion



    //region 挖矿相关:启动挖矿线程、停止挖矿线程、跳过正在挖的矿
    @Override
    public void start() throws Exception {
        while(true){
            Thread.sleep(10);
            if(!mineOption){
                continue;
            }
            MiningBlock miningBlock = miningBlockThreadLocal.get();
            if(!isMiningBlockRight(blockChainDataBase,miningBlock)){
                miningBlock = obtainWrapperBlockForMining(blockChainDataBase);
                miningBlockThreadLocal.set(miningBlock);
            }
            miningBlock(miningBlock);
            //挖矿成功
            if(miningBlock.getMiningSuccess()){
                miningBlockThreadLocal.remove();
                //将矿放入区块链
                boolean isAddBlockToBlockChainSuccess = blockChainDataBase.addBlock(miningBlock.getBlock());
                if(!isAddBlockToBlockChainSuccess){
                    System.err.println("挖矿成功，但是放入区块链失败。");
                    continue;
                }
                //将使用过的交易从挖矿交易数据库中交易
                minerTransactionDtoDataBase.deleteTransactionDtoListByTransactionUuidList(getTransactionUuidList(miningBlock.getForMineBlockTransactionList()));
                minerTransactionDtoDataBase.deleteTransactionDtoListByTransactionUuidList(getTransactionUuidList(miningBlock.getExceptionTransactionList()));
            }
        }
    }

    /**
     * 校验MiningBlock是否正确
     */
    private boolean isMiningBlockRight(BlockChainDataBase blockChainDataBase, MiningBlock miningBlock) throws Exception {
        if(miningBlock == null){
            return false;
        }
        Block block = miningBlock.getBlock();
        if(block == null){
            return false;
        }
        Block tailBlock = blockChainDataBase.findTailNoTransactionBlock();
        if(tailBlock == null){
            if(BigIntegerUtil.isEquals(block.getHeight(),BigInteger.valueOf(1))){
                return true;
            } else {
                return false;
            }
        } else {
            if(BigIntegerUtil.isEquals(tailBlock.getHeight().add(BigInteger.valueOf(1)),block.getHeight()) && tailBlock.getHash().equals(block.getPreviousHash())){
                return true;
            }
            return false;
        }
    }

    private List<String> getTransactionUuidList(List<Transaction> transactionList) {
        if(transactionList == null){
            return null;
        }
        List<String> transactionUuidList = new ArrayList<>();
        for(Transaction transaction:transactionList){
            transactionUuidList.add(transaction.getTransactionUUID());
        }
        return transactionUuidList;
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
     * 获取正在挖矿中的对象
     */
    private MiningBlock obtainWrapperBlockForMining(BlockChainDataBase blockChainDataBase) throws Exception {
        MiningBlock miningBlock = new MiningBlock();
        List<TransactionDTO> forMineBlockTransactionDtoList = minerTransactionDtoDataBase.selectTransactionDtoList(blockChainDataBase,1,10000);
        List<Transaction> forMineBlockTransactionList = new ArrayList<>();
        if(forMineBlockTransactionDtoList != null){
            Iterator<TransactionDTO> iterator = forMineBlockTransactionDtoList.iterator();
            //TODO 写在这里位置不合理
            while (iterator.hasNext()){
                TransactionDTO transactionDTO = iterator.next();
                if(TransactionType.MINER.equals(transactionDTO.getTransactionType())){
                    iterator.remove();
                }
            }
            for(TransactionDTO transactionDTO:forMineBlockTransactionDtoList){
                try {
                    Transaction transaction = NodeTransportUtils.classCast(blockChainDataBase,transactionDTO);
                    forMineBlockTransactionList.add(transaction);
                } catch (Exception e) {
                    logger.info("类型转换异常,将从挖矿交易数据库中删除该交易",e);
                    minerTransactionDtoDataBase.deleteTransactionDtoByTransactionUUID(transactionDTO.getTransactionUUID());
                }
            }
        }
        List<Transaction> exceptionTransactionList = removeExceptionTransaction_PointOfBlockView(blockChainDataBase,forMineBlockTransactionList);
        miningBlock.setExceptionTransactionList(exceptionTransactionList);
        miningBlock.setForMineBlockTransactionList(forMineBlockTransactionList);
        Block nextMineBlock = buildNextMineBlock(blockChainDataBase,forMineBlockTransactionList);

        miningBlock.setBlockChainDataBase(blockChainDataBase);
        miningBlock.setBlock(nextMineBlock);
        miningBlock.setNextNonce(new BigInteger("0"));
        miningBlock.setTryNonceSizeEveryBatch(new BigInteger("10000000"));
        miningBlock.setMiningSuccess(false);
        return miningBlock;
    }

    public void miningBlock(MiningBlock miningBlock) throws Exception {
        //TODO 改善型功能 这里可以利用多处理器的性能进行计算 还可以进行矿池挖矿
        BlockChainDataBase blockChainDataBase = miningBlock.getBlockChainDataBase();
        Block block = miningBlock.getBlock();
        BigInteger startNonce = miningBlock.getNextNonce();
        BigInteger tryNonceSizeEveryBatch = miningBlock.getTryNonceSizeEveryBatch();
        while(true){
            if(!mineOption){
                break;
            }
            BigInteger nextNonce = miningBlock.getNextNonce();
            if(nextNonce.subtract(startNonce).compareTo(tryNonceSizeEveryBatch)>0){
                break;
            }
            block.setNonce(nextNonce);
            block.setHash(BlockUtils.calculateBlockHash(block));
            if(blockChainDataBase.getConsensus().isReachConsensus(blockChainDataBase,block)){
                miningBlock.setMiningSuccess(true);
                break;
            }
            block.setNonce(null);
            miningBlock.setNextNonce(nextNonce.add(new BigInteger("1")));
        }
    }


    /**
     * 挖矿中的区块对象
     * 为了辅助挖矿而创造的类，类里包含了一个需要挖矿的区块和一些辅助挖矿的对象。
     */
    @Data
    public static class MiningBlock {
        //矿工挖矿的区块链
        private BlockChainDataBase blockChainDataBase;
        //矿工要挖矿的区块
        private Block block;
        //标记矿工下一个要验证的nonce
        private BigInteger nextNonce;
        //标记矿工每批次尝试验证的nonce个数
        private BigInteger tryNonceSizeEveryBatch;
        //是否挖矿成功
        private Boolean miningSuccess;
        private List<Transaction> forMineBlockTransactionList;
        private List<Transaction> exceptionTransactionList;
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
            List<TransactionInput> inputs = tx.getInputs();
            boolean multiTimeUseOneUTXO = false;
            for(TransactionInput input:inputs){
                String unspendTransactionOutputUUID = input.getUnspendTransactionOutput().getTransactionOutputUUID();
                if(!uuidSet.add(unspendTransactionOutputUUID)){
                    multiTimeUseOneUTXO = true;
                    break;
                }
            }
            List<TransactionOutput> outputs = tx.getOutputs();
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
    private List<Transaction> removeExceptionTransaction_PointOfTransactionView(BlockChainDataBase blockChainDataBase,List<Transaction> transactionList) throws Exception{
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
    public Transaction buildMineAwardTransaction(BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();

        Transaction transaction = new Transaction();
        transaction.setTimestamp(currentTimeMillis);
        transaction.setTransactionUUID(BlockchainUuidUtil.randomBlockchainUUID(currentTimeMillis));
        transaction.setTransactionType(TransactionType.MINER);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        BigDecimal award = blockChainDataBase.getIncentive().mineAward(blockChainDataBase,block);

        TransactionOutput output = new TransactionOutput();
        output.setStringAddress(minerStringAddress);
        output.setValue(award);
        output.setTransactionOutputUUID(BlockchainUuidUtil.randomBlockchainUUID(currentTimeMillis));

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
        Block tailBlock = blockChainDataBase.findTailNoTransactionBlock();
        Block nonNonceBlock = new Block();
        if(tailBlock == null){
            nonNonceBlock.setHeight(BlockChainCoreConstants.FIRST_BLOCK_HEIGHT);
            nonNonceBlock.setPreviousHash(BlockChainCoreConstants.FIRST_BLOCK_PREVIOUS_HASH);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight().add(BigInteger.valueOf(1)));
            nonNonceBlock.setPreviousHash(tailBlock.getHash());
        }
        nonNonceBlock.setTransactions(packingTransactionList);

        //创建奖励交易，并将奖励加入区块
        Transaction mineAwardTransaction =  buildMineAwardTransaction(blockChainDataBase,nonNonceBlock);
        packingTransactionList.add(mineAwardTransaction);

        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。省去了挖矿时实时更新这个时间的繁琐。
        nonNonceBlock.setTimestamp(mineAwardTransaction.getTimestamp());

        String merkleRoot = BlockUtils.calculateBlockMerkleRoot(nonNonceBlock);
        nonNonceBlock.setMerkleRoot(merkleRoot);

        ConsensusTarget consensusTarget = blockChainDataBase.getConsensus().calculateConsensusTarget(blockChainDataBase,nonNonceBlock);
        nonNonceBlock.setConsensusTarget(consensusTarget);
        return nonNonceBlock;
    }
    //endregion
}
