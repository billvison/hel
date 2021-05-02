package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.enums.BlockchainActionEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.core.tools.*;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDBUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 区块链
 *
 * 注意这是一个线程不安全的实现。在并发的情况下，不保证功能的正确性。
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainDatabaseDefaultImpl extends BlockchainDatabase {

    //region 变量与构造函数
    private static final String BLOCKCHAIN_DATABASE_NAME = "BlockchainDatabase";
    private String blockchianDatabasePath = null;

    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public BlockchainDatabaseDefaultImpl(String rootPath, Incentive incentive, Consensus consensus) {
        super(consensus,incentive);
        blockchianDatabasePath = FileUtil.newPath(rootPath, BLOCKCHAIN_DATABASE_NAME);
    }
    //endregion



    //region 区块增加与删除
    @Override
    public boolean addBlock(Block block) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            boolean isBlockCanAddToBlockchain = isBlockCanAddToBlockchain(block);
            if(!isBlockCanAddToBlockchain){
                return false;
            }
            KvDBUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(block, BlockchainActionEnum.ADD_BLOCK);
            KvDBUtil.write(blockchianDatabasePath, kvWriteBatch);
            return true;
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public void deleteTailBlock() {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            Block tailBlock = queryTailBlock();
            if(tailBlock == null){
                return;
            }
            KvDBUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainActionEnum.DELETE_BLOCK);
            KvDBUtil.write(blockchianDatabasePath,kvWriteBatch);
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public void deleteBlocks(long blockHeight) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            while (true){
                Block tailBlock = queryTailBlock();
                if(tailBlock == null){
                    return;
                }
                if(LongUtil.isLessThan(tailBlock.getHeight(),blockHeight)){
                    return;
                }
                KvDBUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainActionEnum.DELETE_BLOCK);
                KvDBUtil.write(blockchianDatabasePath,kvWriteBatch);
            }
        }finally {
            writeLock.unlock();
        }
    }
    //endregion



    //region 校验区块、交易
    @Override
    public boolean isBlockCanAddToBlockchain(Block block) {
        //检查系统版本是否支持
        if(!GlobalSetting.SystemVersionConstant.isVersionLegal(block.getHeight())){
            LogUtil.debug("系统版本过低，不支持校验区块，请尽快升级系统。");
            return false;
        }

        //校验区块的结构
        if(!StructureTool.isBlockStructureLegal(block)){
            LogUtil.debug("区块数据异常，请校验区块的结构。");
            return false;
        }
        //校验区块的存储容量
        if(!SizeTool.isBlockStorageCapacityLegal(block)){
            LogUtil.debug("区块数据异常，请校验区块的大小。");
            return false;
        }

        Block previousBlock = queryTailBlock();
        //校验区块写入的属性值
        if(!BlockPropertyTool.isWritePropertiesRight(previousBlock,block)){
            LogUtil.debug("区块校验失败：区块的属性写入值与实际计算结果不一致。");
            return false;
        }

        //校验业务
        //校验区块时间
        if(!BlockTool.isBlockTimestampLegal(previousBlock,block)){
            LogUtil.debug("区块生成的时间太滞后。");
            return false;
        }
        //新产生的哈希是否合法
        if(isNewHashIllegal(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //新产生的地址是否合法
        if(isAddressIllegal(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //双花校验
        if(isDoubleSpentAttackHappen(block)){
            LogUtil.debug("区块数据异常，检测到双花攻击。");
            return false;
        }
        //校验共识
        if(!isReachConsensus(block)){
            LogUtil.debug("区块数据异常，未满足共识规则。");
            return false;
        }
        //校验激励
        if(!isIncentiveRight(block)){
            LogUtil.debug("区块数据异常，未满足共识规则。");
            return false;
        }

        //从交易角度校验每一笔交易
        for(Transaction transaction : block.getTransactions()){
            boolean transactionCanAddToNextBlock = isTransactionCanAddToNextBlock(transaction);
            if(!transactionCanAddToNextBlock){
                LogUtil.debug("区块数据异常，交易异常。");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isTransactionCanAddToNextBlock(Transaction transaction) {
        //校验交易的结构
        if(!StructureTool.isTransactionStructureLegal(transaction)){
            LogUtil.debug("交易数据异常，请校验交易的结构。");
            return false;
        }
        //校验交易的存储容量
        if(!SizeTool.isTransactionStorageCapacityLegal(transaction)){
            LogUtil.debug("交易数据异常，请校验交易的大小。");
            return false;
        }

        //校验交易的属性是否与计算得来的一致
        if(!TransactionPropertyTool.isWritePropertiesRight(transaction)){
            return false;
        }

        //交易地址是否合法
        if(TransactionTool.isTransactionAddressIllegal(transaction)){
            return false;
        }

        //业务校验
        //校验交易金额
        if(!TransactionTool.isTransactionAmountLegal(transaction)){
            LogUtil.debug("交易金额不合法");
            return false;
        }
        //校验是否双花
        if(isDoubleSpentAttackHappen(transaction)){
            LogUtil.debug("交易数据异常，检测到双花攻击。");
            return false;
        }
        //新产生的哈希是否合法
        if(isNewHashIllegal(transaction)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //新产生的地址是否合法
        if(isNewAddressIllegal(transaction)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }

        //根据交易类型，做进一步的校验
        if(transaction.getTransactionType() == TransactionType.COINBASE){
            //校验激励，在区块层面进行校验
            return true;
        } else if(transaction.getTransactionType() == TransactionType.NORMAL){
            //交易输入必须要大于等于交易输出
            long inputsValue = TransactionTool.getInputsValue(transaction);
            long outputsValue = TransactionTool.getOutputsValue(transaction);
            if(inputsValue < outputsValue) {
                LogUtil.debug("交易校验失败：交易的输入必须大于等于交易的输出。不合法的交易。");
                return false;
            }
            //脚本
            if(!TransactionTool.verifyScript(transaction)) {
                LogUtil.debug("交易校验失败：交易[输入脚本]解锁交易[输出脚本]异常。");
                return false;
            }
            return true;
        } else {
            LogUtil.debug("区块数据异常，不能识别的交易类型。");
            return false;
        }
    }
    //endregion



    //region 普通查询
    @Override
    public long queryBlockchainHeight() {
        byte[] bytesBlockchainHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildBlockchainHeightKey());
        if(bytesBlockchainHeight == null){
            return GlobalSetting.GenesisBlock.HEIGHT;
        }
        return ByteUtil.decodeToLong(bytesBlockchainHeight);
    }

    @Override
    public long queryBlockchainTransactionHeight() {
        byte[] byteTotalTransactionCount = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey());
        if(byteTotalTransactionCount == null){
            return 0;
        }
        return ByteUtil.decodeToLong(byteTotalTransactionCount);
    }
    @Override
    public long queryBlockchainTransactionOutputHeight() {
        byte[] byteTotalTransactionCount = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildBlockchainTransactionOutputHeightKey());
        if(byteTotalTransactionCount == null){
            return 0;
        }
        return ByteUtil.decodeToLong(byteTotalTransactionCount);
    }
    //endregion



    //region 区块查询
    @Override
    public Block queryTailBlock() {
        long blockchainHeight = queryBlockchainHeight();
        if(LongUtil.isLessEqualThan(blockchainHeight, GlobalSetting.GenesisBlock.HEIGHT)){
            return null;
        }
        return queryBlockByBlockHeight(blockchainHeight);
    }
    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        byte[] bytesBlock = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeTool.decodeToBlock(bytesBlock);
    }
    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        byte[] bytesBlockHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(blockHash));
        if(bytesBlockHeight == null){
            return null;
        }
        return queryBlockByBlockHeight(ByteUtil.decodeToLong(bytesBlockHeight));
    }
    //endregion



    //region 交易查询
    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        byte[] transactionHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transactionHash));
        if(transactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.decodeToLong(transactionHeight));
    }

    @Override
    public Transaction querySourceTransactionByTransactionOutputId(TransactionOutputId transactionOutputId) {
        byte[] sourceTransactionHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionOutputIdToSourceTransactionHeightKey(transactionOutputId));
        if(sourceTransactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.decodeToLong(sourceTransactionHeight));
    }

    @Override
    public Transaction queryDestinationTransactionByTransactionOutputId(TransactionOutputId transactionOutputId) {
        byte[] destinationTransactionHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionOutputIdToDestinationTransactionHeightKey(transactionOutputId));
        if(destinationTransactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.decodeToLong(destinationTransactionHeight));
    }

    @Override
    public TransactionOutput queryTransactionOutputByTransactionOutputHeight(long transactionOutputHeight) {
        byte[] bytesTransactionOutput = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionOutputHeightToTransactionOutputKey(transactionOutputHeight));
        if(bytesTransactionOutput == null){
            return null;
        }
        return EncodeDecodeTool.decodeToTransactionOutput(bytesTransactionOutput);
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        byte[] byteTransaction = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transactionHeight));
        if(byteTransaction == null){
            return null;
        }
        Transaction transaction = EncodeDecodeTool.decodeToTransaction(byteTransaction);
        return transaction;
    }
    //endregion



    //region 交易输出查询
    public TransactionOutput queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) {
        byte[] bytesTransactionOutputHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionOutputIdToTransactionOutputHeightKey(transactionOutputId));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.decodeToLong(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryUnspentTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) {
        byte[] bytesTransactionOutputHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(transactionOutputId));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.decodeToLong(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput querySpentTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) {
        byte[] bytesTransactionOutputHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(transactionOutputId));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.decodeToLong(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildAddressToTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.decodeToLong(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryUnspentTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.decodeToLong(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput querySpentTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildAddressToSpentTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.decodeToLong(bytesTransactionOutputHeight));
    }
    //endregion



    //region 拼装WriteBatch
    /**
     * 根据区块信息组装WriteBatch对象
     */
    private KvDBUtil.KvWriteBatch createBlockWriteBatch(Block block, BlockchainActionEnum blockchainActionEnum) {
        fillBlockProperty(block);
        KvDBUtil.KvWriteBatch kvWriteBatch = new KvDBUtil.KvWriteBatch();

        storeHash(kvWriteBatch,block,blockchainActionEnum);
        storeAddress(kvWriteBatch,block,blockchainActionEnum);

        storeBlockchainHeight(kvWriteBatch,block,blockchainActionEnum);
        storeBlockchainTransactionHeight(kvWriteBatch,block,blockchainActionEnum);
        storeBlockchainTransactionOutputHeight(kvWriteBatch,block,blockchainActionEnum);

        storeBlockHeightToBlock(kvWriteBatch,block,blockchainActionEnum);
        storeBlockHashToBlockHeight(kvWriteBatch,block,blockchainActionEnum);

        storeTransactionHeightToTransaction(kvWriteBatch,block,blockchainActionEnum);
        storeTransactionHashToTransactionHeight(kvWriteBatch,block,blockchainActionEnum);

        storeTransactionOutputHeightToTransactionOutput(kvWriteBatch,block,blockchainActionEnum);
        storeTransactionOutputIdToTransactionOutputHeight(kvWriteBatch,block,blockchainActionEnum);
        storeTransactionOutputIdToUnspentTransactionOutputHeight(kvWriteBatch,block,blockchainActionEnum);
        storeTransactionOutputIdToSpentTransactionOutputHeight(kvWriteBatch,block,blockchainActionEnum);
        storeTransactionOutputIdToSourceTransactionHeight(kvWriteBatch,block,blockchainActionEnum);
        storeTransactionOutputIdToDestinationTransactionHeight(kvWriteBatch,block,blockchainActionEnum);

        storeAddressToTransactionOutputHeight(kvWriteBatch,block,blockchainActionEnum);
        storeAddressToUnspentTransactionOutputHeight(kvWriteBatch,block,blockchainActionEnum);
        storeAddressToSpentTransactionOutputHeight(kvWriteBatch,block,blockchainActionEnum);
        return kvWriteBatch;
    }

    /**
     * 补充区块的属性
     */
    private void fillBlockProperty(Block block) {
        long transactionIndex = 1;
        long transactionHeight = queryBlockchainTransactionHeight();
        long transactionOutputHeight = queryBlockchainTransactionOutputHeight();
        long blockHeight = block.getHeight();
        String blockHash = block.getHash();
        List<Transaction> transactions = block.getTransactions();
        long transactionCount = BlockTool.getTransactionCount(block);
        block.setTransactionCount(transactionCount);
        block.setPreviousTransactionHeight(queryBlockchainTransactionHeight());
        if(transactions != null){
            for(Transaction transaction:transactions){
                //下一个交易高度
                transactionHeight++;
                transaction.setBlockHeight(blockHeight);
                transaction.setTransactionIndex(transactionIndex);
                transaction.setTransactionHeight(transactionHeight);

                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for (int i=0; i <outputs.size(); i++){
                        //下一个交易输出高度
                        transactionOutputHeight++;
                        TransactionOutput transactionOutput = outputs.get(i);
                        transactionOutput.setBlockHeight(blockHeight);
                        transactionOutput.setBlockHash(blockHash);
                        transactionOutput.setTransactionHeight(transactionHeight);
                        transactionOutput.setTransactionHash(transaction.getTransactionHash());
                        transactionOutput.setTransactionOutputIndex(i+1);
                        transactionOutput.setTransactionIndex(transaction.getTransactionIndex());
                        transactionOutput.setTransactionOutputHeight(transactionOutputHeight);
                    }
                }
            }
        }
    }
    /**
     * [交易输出ID]到[来源交易高度]的映射
     */
    private void storeTransactionOutputIdToSourceTransactionHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput transactionOutput:outputs){
                        byte[] transactionOutputIdToToSourceTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSourceTransactionHeightKey(transactionOutput);
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToToSourceTransactionHeightKey, ByteUtil.encode(transaction.getTransactionHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToToSourceTransactionHeightKey);
                        }
                    }
                }
            }
        }
    }
    /**
     * [已花费交易输出ID]到[去向交易高度]的映射
     */
    private void storeTransactionOutputIdToDestinationTransactionHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToToDestinationTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToDestinationTransactionHeightKey(unspentTransactionOutput);
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToToDestinationTransactionHeightKey, ByteUtil.encode(transaction.getTransactionHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToToDestinationTransactionHeightKey);
                        }
                    }
                }
            }
        }
    }
    /**
     * [交易输出ID]到[交易输出]的映射
     */
    private void storeTransactionOutputIdToTransactionOutputHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToTransactionOutputHeightKey(output);
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToTransactionOutputHeightKey, ByteUtil.encode(output.getTransactionOutputHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToTransactionOutputHeightKey);
                        }
                    }
                }
            }
        }
    }
    /**
     * [交易输出高度]到[交易输出]的映射
     */
    private void storeTransactionOutputHeightToTransactionOutput(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputHeightToTransactionOutputKey = BlockchainDatabaseKeyTool.buildTransactionOutputHeightToTransactionOutputKey(output.getTransactionOutputHeight());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputHeightToTransactionOutputKey, EncodeDecodeTool.encode(output));
                        } else {
                            kvWriteBatch.delete(transactionOutputHeightToTransactionOutputKey);
                        }
                    }
                }
            }
        }
    }
    /**
     * 存储未花费交易输出ID到未花费交易输出的映射
     */
    private void storeTransactionOutputIdToUnspentTransactionOutputHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(unspentTransactionOutput);
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.delete(transactionOutputIdToUnspentTransactionOutputHeightKey);
                        } else {
                            kvWriteBatch.put(transactionOutputIdToUnspentTransactionOutputHeightKey, ByteUtil.encode(unspentTransactionOutput.getTransactionOutputHeight()));
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(output);
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToUnspentTransactionOutputHeightKey, ByteUtil.encode(output.getTransactionOutputHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToUnspentTransactionOutputHeightKey);
                        }
                    }
                }
            }
        }
    }
    /**
     * 存储已花费交易输出ID到已花费交易输出的映射
     */
    private void storeTransactionOutputIdToSpentTransactionOutputHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(unspentTransactionOutput);
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToSpentTransactionOutputHeightKey, ByteUtil.encode(unspentTransactionOutput.getTransactionOutputHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToSpentTransactionOutputHeightKey);
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(output);
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.delete(transactionOutputIdToSpentTransactionOutputHeightKey);
                        } else {
                            kvWriteBatch.put(transactionOutputIdToSpentTransactionOutputHeightKey, ByteUtil.encode(output.getTransactionOutputHeight()));
                        }
                    }
                }
            }
        }
    }
    /**
     * 存储交易高度到交易的映射
     */
    private void storeTransactionHeightToTransaction(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                //更新区块链中的交易序列号数据
                byte[] transactionHeightToTransactionKey = BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transaction.getTransactionHeight());
                if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                    kvWriteBatch.put(transactionHeightToTransactionKey, EncodeDecodeTool.encode(transaction));
                } else {
                    kvWriteBatch.delete(transactionHeightToTransactionKey);
                }
            }
        }
    }
    /**
     * 存储交易哈希到交易高度的映射
     */
    private void storeTransactionHashToTransactionHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                byte[] transactionHashToTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transaction.getTransactionHash());
                if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                    kvWriteBatch.put(transactionHashToTransactionHeightKey, ByteUtil.encode(transaction.getTransactionHeight()));
                } else {
                    kvWriteBatch.delete(transactionHashToTransactionHeightKey);
                }
            }
        }
    }
    /**
     * 存储区块链的高度
     */
    private void storeBlockchainHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockchainHeightKey = BlockchainDatabaseKeyTool.buildBlockchainHeightKey();
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.encode(block.getHeight()));
        }else{
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.encode(block.getHeight()-1));
        }
    }
    /**
     * 存储区块哈希到区块高度的映射
     */
    private void storeBlockHashToBlockHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockHashBlockHeightKey = BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(block.getHash());
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockHashBlockHeightKey, ByteUtil.encode(block.getHeight()));
        }else{
            kvWriteBatch.delete(blockHashBlockHeightKey);
        }
    }
    /**
     * 存储区块链中总的交易高度
     */
    private void storeBlockchainTransactionHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        long transactionCount = queryBlockchainTransactionHeight();
        byte[] bytesBlockchainTransactionCountKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey();
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.encode(transactionCount + BlockTool.getTransactionCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.encode(transactionCount - BlockTool.getTransactionCount(block)));
        }
    }
    /**
     * 存储区块链中总的交易数量
     */
    private void storeBlockchainTransactionOutputHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        long transactionOutputCount = queryBlockchainTransactionOutputHeight();
        byte[] bytesBlockchainTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionOutputHeightKey();
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.encode(transactionOutputCount + BlockTool.getTransactionOutputCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.encode(transactionOutputCount - BlockTool.getTransactionOutputCount(block)));
        }
    }
    /**
     * 存储区块链高度到区块的映射
     */
    private void storeBlockHeightToBlock(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockHeightKey = BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(block.getHeight());
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockHeightKey, EncodeDecodeTool.encode(block));
        }else{
            kvWriteBatch.delete(blockHeightKey);
        }
    }

    /**
     * 存储已使用的哈希
     */
    private void storeHash(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockHashKey = BlockchainDatabaseKeyTool.buildHashKey(block.getHash());
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockHashKey, blockHashKey);
        } else {
            kvWriteBatch.delete(blockHashKey);
        }
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                byte[] transactionHashKey = BlockchainDatabaseKeyTool.buildHashKey(transaction.getTransactionHash());
                if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                    kvWriteBatch.put(transactionHashKey, transactionHashKey);
                } else {
                    kvWriteBatch.delete(transactionHashKey);
                }
            }
        }
    }

    /**
     * 存储已使用的地址
     */
    private void storeAddress(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] addressKey = BlockchainDatabaseKeyTool.buildAddressKey(output.getAddress());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(addressKey, addressKey);
                        } else {
                            kvWriteBatch.delete(addressKey);
                        }
                    }
                }
            }
        }
    }
    /**
     * 存储地址到未花费交易输出列表
     */
    private void storeAddressToUnspentTransactionOutputHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        for(Transaction transaction : block.getTransactions()){
            if(transaction == null){
                return;
            }
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspentTransactionOutput();
                    byte[] addressToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(utxo.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.delete(addressToUnspentTransactionOutputHeightKey);
                    }else{
                        kvWriteBatch.put(addressToUnspentTransactionOutputHeightKey, ByteUtil.encode(utxo.getTransactionOutputHeight()));
                    }
                }
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (TransactionOutput transactionOutput:outputs){
                    byte[] addressToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(transactionOutput.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.put(addressToUnspentTransactionOutputHeightKey, ByteUtil.encode(transactionOutput.getTransactionOutputHeight()));
                    }else{
                        kvWriteBatch.delete(addressToUnspentTransactionOutputHeightKey);
                    }
                }
            }
        }
    }
    /**
     * 存储地址到交易输出
     */
    private void storeAddressToTransactionOutputHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        for(Transaction transaction : block.getTransactions()){
            if(transaction == null){
                return;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (TransactionOutput transactionOutput:outputs){
                    byte[] addressToTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToTransactionOutputHeightKey(transactionOutput.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.put(addressToTransactionOutputHeightKey, ByteUtil.encode(transactionOutput.getTransactionOutputHeight()));
                    }else{
                        kvWriteBatch.delete(addressToTransactionOutputHeightKey);
                    }
                }
            }
        }
    }
    /**
     * 存储地址到交易输出高度
     */
    private void storeAddressToSpentTransactionOutputHeight(KvDBUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        for(Transaction transaction : block.getTransactions()){
            if(transaction == null){
                return;
            }
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspentTransactionOutput();
                    byte[] addressToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToSpentTransactionOutputHeightKey(utxo.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.put(addressToSpentTransactionOutputHeightKey, ByteUtil.encode(utxo.getTransactionOutputHeight()));
                    }else{
                        kvWriteBatch.delete(addressToSpentTransactionOutputHeightKey);
                    }
                }
            }
        }
    }
    //endregion


    /**
     * 检查交易输入是否都是未花费交易输出
     */
    private boolean isTransactionInputFromUnspentTransactionOutput(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                TransactionOutput transactionOutput = queryUnspentTransactionOutputByTransactionOutputId(unspentTransactionOutput);
                if(transactionOutput == null){
                    LogUtil.debug("交易数据异常：交易输入不是未花费交易输出。");
                    return false;
                }
            }
        }
        return true;
    }


    //region 新产生的哈希相关
    /**
     * 哈希是否已经被区块链系统使用了？
     */
    private boolean isHashUsed(String hash){
        byte[] bytesHash = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildHashKey(hash));
        return bytesHash != null;
    }
    /**
     * 交易中新产生的哈希是否已经被区块链系统使用了？
     */
    private boolean isNewHashUsed(Transaction transaction) {
        //校验交易Hash是否已经被使用了
        String transactionHash = transaction.getTransactionHash();
        if(isHashUsed(transactionHash)){
            LogUtil.debug("交易数据异常，交易Hash已经被使用了。");
            return true;
        }
        return false;
    }
    /**
     * 区块中新产生的哈希是否已经被区块链系统使用了？
     */
    private boolean isHashUsed(Block block) {
        //校验区块Hash是否已经被使用了
        String blockHash = block.getHash();
        if(isHashUsed(blockHash)){
            LogUtil.debug("区块数据异常，区块Hash已经被使用了。");
            return true;
        }
        //校验每一笔交易新产生的Hash是否正确
        List<Transaction> blockTransactions = block.getTransactions();
        if(blockTransactions != null){
            for(Transaction transaction:blockTransactions){
                if(isNewHashUsed(transaction)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 区块中新产生的哈希是否合法
     */
    private boolean isNewHashIllegal(Transaction transaction) {
        //校验哈希作为主键的正确性
        //新产生的Hash不能被使用过
        if(isNewHashUsed(transaction)){
            LogUtil.debug("校验数据异常，校验中占用的部分主键已经被使用了。");
            return true;
        }
        return false;
    }
    /**
     * 区块中新产生的哈希是否合法
     */
    private boolean isNewHashIllegal(Block block) {
        //校验哈希作为主键的正确性
        //新产生的哈希不能有重复
        if(BlockTool.isExistDuplicateNewHash(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希有重复。");
            return true;
        }
        //新产生的哈希不能被区块链使用过了
        if(isHashUsed(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希已经早被区块链使用了。");
            return true;
        }
        return false;
    }
    private boolean isAddressIllegal(Block block) {
        //校验地址作为主键的正确性
        //新产生的地址不能有重复
        if(BlockTool.isExistDuplicateNewAddress(block)){
            LogUtil.debug("区块数据异常，区块中新产生的地址有重复。");
            return true;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(isNewAddressIllegal(transaction)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 区块中新产生的哈希是否合法
     */
    private boolean isNewAddressIllegal(Transaction transaction) {
        if(TransactionTool.isExistDuplicateNewAddress(transaction)){
            return true;
        }
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs){
                String address = output.getAddress();
                if(isAddressUsed(address)){
                    LogUtil.debug(String.format("区块数据异常，地址[%s]重复。",address));
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isAddressUsed(String address) {
        byte[] bytesAddress = KvDBUtil.get(blockchianDatabasePath, BlockchainDatabaseKeyTool.buildAddressKey(address));
        return bytesAddress != null;
    }
    //endregion


    //region 双花攻击
    /**
     * 是否有双花攻击
     */
    private boolean isDoubleSpentAttackHappen(Transaction transaction) {
        //双花交易：交易内部存在重复的(未花费交易输出)
        if(TransactionTool.isExistDuplicateTransactionInput(transaction)){
            LogUtil.debug("交易数据异常，检测到双花攻击。");
            return true;
        }
        //双花交易：交易内部存在已经花费的(未花费交易输出)
        if(!isTransactionInputFromUnspentTransactionOutput(transaction)){
            LogUtil.debug("交易数据异常：发生双花交易。");
            return true;
        }
        return false;
    }

    /**
     * 是否有双花攻击
     */
    private boolean isDoubleSpentAttackHappen(Block block) {
        //双花交易：区块内部存在重复的(未花费交易输出)
        if(BlockTool.isExistDuplicateTransactionInput(block)){
            LogUtil.debug("区块数据异常：发生双花交易。");
            return true;
        }
        //双花交易：区块内部存在已经花费的(未花费交易输出)
        for(Transaction transaction : block.getTransactions()){
            if(!isTransactionInputFromUnspentTransactionOutput(transaction)){
                LogUtil.debug("区块数据异常：发生双花交易。");
                return true;
            }
        }
        return false;
    }
    //endregion

    /**
     * 区块激励正确吗？
     */
    private boolean isIncentiveRight(Block block) {
        long writeIncentiveValue = BlockTool.getMinerIncentiveValue(block);
        long targetIncentiveValue = incentive.incentiveAmount(block);
        if(writeIncentiveValue != targetIncentiveValue){
            LogUtil.debug("区块数据异常，挖矿奖励数据异常。");
            return false;
        }
        return true;
    }
    /**
     * 区块满足共识规则吗？
     */
    private boolean isReachConsensus(Block block) {
        return consensus.isReachConsensus(this,block);
    }
}