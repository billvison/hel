package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Miner;
import com.xingkaichun.helloworldblockchain.core.MinerTransactionDtoDataBase;
import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.CommunityMaintenanceTransactionTool;
import com.xingkaichun.helloworldblockchain.core.tools.NodeTransportDtoTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 默认实现
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class MinerDefaultImpl extends Miner {

    private Logger logger = LoggerFactory.getLogger(MinerDefaultImpl.class);

    //region 属性与构造函数
    //挖矿开关:默认打开挖矿的开关
    private boolean mineOption = true;

    /**
     * 存储正在挖矿中的区块
     */
    private ThreadLocal<MiningBlock> miningBlockThreadLocal;

    public MinerDefaultImpl(BlockChainDataBase blockChainDataBase, MinerTransactionDtoDataBase minerTransactionDtoDataBase, String minerAddress) {
        super(minerAddress,blockChainDataBase,minerTransactionDtoDataBase);
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
            //重新组装MiningBlock
            if(isObtainMiningBlockAgain(blockChainDataBase,miningBlock)){
                miningBlock = obtainMiningBlock(blockChainDataBase);
                miningBlockThreadLocal.set(miningBlock);
            }
            miningBlock(blockChainDataBase,miningBlock);
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
                minerTransactionDtoDataBase.deleteTransactionDtoListByTransactionHashList(getTransactionHashList(miningBlock.getForMineBlockTransactionList()));
                minerTransactionDtoDataBase.deleteTransactionDtoListByTransactionHashList(getTransactionHashList(miningBlock.getExceptionTransactionList()));
            }
        }
    }

    /**
     * 校验MiningBlock是否正确
     */
    private boolean isObtainMiningBlockAgain(BlockChainDataBase blockChainDataBase, MiningBlock miningBlock) throws Exception {
        if(miningBlock == null){
            return true;
        }
        //挖矿超过一定时长还没有挖矿成功，这时重新打包交易，对自己来说，可以获取更多的奖励，对交易发送者来说，可以让自己的交易更快的写进区块链
        //一定时间内还没有挖到矿，重新开始挖矿。
        if(System.currentTimeMillis() > miningBlock.getStartTimestamp()+ GlobalSetting.MinerConstant.MAX_MINE_TIMESTAMP){
            return true;
        }
        Block block = miningBlock.getBlock();
        if(block == null){
            return true;
        }
        Block tailBlock = blockChainDataBase.findTailNoTransactionBlock();
        if(tailBlock == null){
            //第一个区块，除了创始人，其它矿工没有机会走到这里
            return false;
        } else {
            if(BigIntegerUtil.isEquals(tailBlock.getHeight().add(BigInteger.valueOf(1)),block.getHeight()) && tailBlock.getHash().equals(block.getPreviousBlockHash())){
                return false;
            }
            return true;
        }
    }

    private List<String> getTransactionHashList(List<Transaction> transactionList) {
        if(transactionList == null){
            return null;
        }
        List<String> transactionHashList = new ArrayList<>();
        for(Transaction transaction:transactionList){
            transactionHashList.add(transaction.getTransactionHash());
        }
        return transactionHashList;
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
    private MiningBlock obtainMiningBlock(BlockChainDataBase blockChainDataBase) throws Exception {
        MiningBlock miningBlock = new MiningBlock();
        List<TransactionDTO> forMineBlockTransactionDtoList = minerTransactionDtoDataBase.selectTransactionDtoList(blockChainDataBase,1,10000);
        List<Transaction> forMineBlockTransactionList = new ArrayList<>();
        if(forMineBlockTransactionDtoList != null){
            for(TransactionDTO transactionDTO:forMineBlockTransactionDtoList){
                try {
                    Transaction transaction = NodeTransportDtoTool.classCast(blockChainDataBase,transactionDTO);
                    forMineBlockTransactionList.add(transaction);
                } catch (Exception e) {
                    logger.info("类型转换异常,将从挖矿交易数据库中删除该交易",e);
                    minerTransactionDtoDataBase.deleteTransactionDto(transactionDTO);
                }
            }
        }
        List<Transaction> exceptionTransactionList = removeExceptionTransaction_PointOfBlockView(blockChainDataBase,forMineBlockTransactionList);
        miningBlock.setExceptionTransactionList(exceptionTransactionList);
        miningBlock.setForMineBlockTransactionList(forMineBlockTransactionList);
        Block nextMineBlock = buildNextMineBlock(blockChainDataBase,forMineBlockTransactionList);

        miningBlock.setStartTimestamp(System.currentTimeMillis());
        miningBlock.setBlockChainDataBase(blockChainDataBase);
        miningBlock.setBlock(nextMineBlock);
        miningBlock.setNextNonce(new BigInteger("0"));
        miningBlock.setTryNonceSizeEveryBatch(new BigInteger("10000000"));
        miningBlock.setMiningSuccess(false);
        return miningBlock;
    }

    public void miningBlock(BlockChainDataBase blockChainDataBase, MiningBlock miningBlock) throws Exception {
        //TODO 改善型功能 这里可以利用多处理器的性能进行计算 还可以进行矿池挖矿
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
            block.setConsensusValue(nextNonce.toString());
            block.setHash(BlockTool.calculateBlockHash(block));
            if(blockChainDataBase.getConsensus().isReachConsensus(blockChainDataBase,block)){
                miningBlock.setMiningSuccess(true);
                break;
            }
            block.setConsensusValue(null);
            miningBlock.setNextNonce(nextNonce.add(new BigInteger("1")));
        }
    }


    /**
     * 挖矿中的区块对象
     * 为了辅助挖矿而创造的类，类里包含了一个需要挖矿的区块和一些辅助挖矿的对象。
     */
    public static class MiningBlock {
        //挖矿开始的时间戳
        private long startTimestamp;
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




        //region get set

        public long getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public BlockChainDataBase getBlockChainDataBase() {
            return blockChainDataBase;
        }

        public void setBlockChainDataBase(BlockChainDataBase blockChainDataBase) {
            this.blockChainDataBase = blockChainDataBase;
        }

        public Block getBlock() {
            return block;
        }

        public void setBlock(Block block) {
            this.block = block;
        }

        public BigInteger getNextNonce() {
            return nextNonce;
        }

        public void setNextNonce(BigInteger nextNonce) {
            this.nextNonce = nextNonce;
        }

        public BigInteger getTryNonceSizeEveryBatch() {
            return tryNonceSizeEveryBatch;
        }

        public void setTryNonceSizeEveryBatch(BigInteger tryNonceSizeEveryBatch) {
            this.tryNonceSizeEveryBatch = tryNonceSizeEveryBatch;
        }

        public Boolean getMiningSuccess() {
            return miningSuccess;
        }

        public void setMiningSuccess(Boolean miningSuccess) {
            this.miningSuccess = miningSuccess;
        }

        public List<Transaction> getForMineBlockTransactionList() {
            return forMineBlockTransactionList;
        }

        public void setForMineBlockTransactionList(List<Transaction> forMineBlockTransactionList) {
            this.forMineBlockTransactionList = forMineBlockTransactionList;
        }

        public List<Transaction> getExceptionTransactionList() {
            return exceptionTransactionList;
        }

        public void setExceptionTransactionList(List<Transaction> exceptionTransactionList) {
            this.exceptionTransactionList = exceptionTransactionList;
        }

        //endregion
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

        Set<String> hashSet = new HashSet<>();
        Iterator<Transaction> iterator = packingTransactionList.iterator();
        while (iterator.hasNext()){
            Transaction tx = iterator.next();
            List<TransactionInput> inputs = tx.getInputs();
            boolean multiTimeUseOneUTXO = false;
            //同一张钱不能被两次交易同时使用【同一个UTXO不允许出现在不同的交易中】
            for(TransactionInput input:inputs){
                String unspendTransactionOutputHash = input.getUnspendTransactionOutput().getTransactionOutputHash();
                if(!hashSet.add(unspendTransactionOutputHash)){
                    multiTimeUseOneUTXO = true;
                    break;
                }
            }
            //校验新产生的哈希的唯一性
            List<TransactionOutput> outputs = tx.getOutputs();
            for(TransactionOutput transactionOutput:outputs){
                String transactionOutputHash = transactionOutput.getTransactionOutputHash();
                if(!hashSet.add(transactionOutputHash)){
                    multiTimeUseOneUTXO = true;
                    break;
                }
            }
            if(multiTimeUseOneUTXO){
                iterator.remove();
                exceptionTransactionList.add(tx);
                logger.debug("交易校验失败：交易的输入中同一个UTXO被多次使用。不合法的交易。");
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
    public Transaction buildMineAwardTransaction(long timestamp, BlockChainDataBase blockChainDataBase, Block block) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(timestamp);
        transaction.setTransactionType(TransactionType.MINER_AWARD);
        transaction.setInputs(null);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        BigDecimal award = blockChainDataBase.getIncentive().mineAward(blockChainDataBase,block);

        TransactionOutput output = new TransactionOutput();
        output.setAddress(minerAddress);
        output.setValue(award);
        output.setScriptLock(StackBasedVirtualMachine.createPayToClassicAddressOutputScript(minerAddress));
        output.setTransactionOutputHash(TransactionTool.calculateTransactionOutputHash(transaction,output));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transaction));
        return transaction;
    }

    //endregion

    //region 构建区块、计算区块hash、校验区块Nonce
    /**
     * 构建挖矿区块
     */
    public Block buildNextMineBlock(BlockChainDataBase blockChainDataBase, List<Transaction> packingTransactionList) throws Exception {
        long timestamp = System.currentTimeMillis();

        Block tailBlock = blockChainDataBase.findTailNoTransactionBlock();
        Block nonNonceBlock = new Block();
        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。省去了挖矿时实时更新这个时间的繁琐。
        nonNonceBlock.setTimestamp(timestamp);

        if(tailBlock == null){
            nonNonceBlock.setHeight(GlobalSetting.GenesisBlockConstant.FIRST_BLOCK_HEIGHT);
            nonNonceBlock.setPreviousBlockHash(GlobalSetting.GenesisBlockConstant.FIRST_BLOCK_PREVIOUS_HASH);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight().add(BigInteger.valueOf(1)));
            nonNonceBlock.setPreviousBlockHash(tailBlock.getHash());
        }
        nonNonceBlock.setTransactions(packingTransactionList);

        //社区维护
        Transaction maintenanceTransaction = CommunityMaintenanceTransactionTool.obtainMaintenanceTransaction(timestamp,nonNonceBlock.getHeight());
        if(maintenanceTransaction != null){
            packingTransactionList.add(maintenanceTransaction);
        }

        //创建挖矿奖励交易
        Transaction mineAwardTransaction =  buildMineAwardTransaction(timestamp,blockChainDataBase,nonNonceBlock);
        packingTransactionList.add(mineAwardTransaction);


        String merkleRoot = BlockTool.calculateBlockMerkleRoot(nonNonceBlock);
        nonNonceBlock.setMerkleRoot(merkleRoot);
        return nonNonceBlock;
    }
    //endregion
}
