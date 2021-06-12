package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Consensus;
import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.enums.BlockchainActionEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.tools.*;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.setting.Setting;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 区块链
 * 注意这是一个线程不安全的实现。在并发的情况下，不保证功能的正确性。
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainDatabaseDefaultImpl extends BlockchainDatabase {

    //region 变量与构造函数
    CoreConfiguration coreConfiguration;
    private static final String BLOCKCHAIN_DATABASE_NAME = "BlockchainDatabase";

    /**
     * 锁:保证对区块链增区块、删区块的操作是同步的。
     * 查询区块操作不需要加锁，原因是，只有对区块链进行区块的增删才会改变区块链的数据。
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public BlockchainDatabaseDefaultImpl(CoreConfiguration coreConfiguration, Incentive incentive, Consensus consensus) {
        super(consensus,incentive);
        this.coreConfiguration = coreConfiguration;
    }
    //endregion



    //region 区块增加与删除
    @Override
    public boolean addBlock(Block block) {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try{
            boolean checkBlock = checkBlock(block);
            if(!checkBlock){
                return false;
            }
            KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(block, BlockchainActionEnum.ADD_BLOCK);
            KvDbUtil.write(getBlockchainDatabasePath(), kvWriteBatch);
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
            KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainActionEnum.DELETE_BLOCK);
            KvDbUtil.write(getBlockchainDatabasePath(),kvWriteBatch);
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
                KvDbUtil.KvWriteBatch kvWriteBatch = createBlockWriteBatch(tailBlock, BlockchainActionEnum.DELETE_BLOCK);
                KvDbUtil.write(getBlockchainDatabasePath(),kvWriteBatch);
            }
        }finally {
            writeLock.unlock();
        }
    }
    //endregion



    //region 校验区块、交易
    @Override
    public boolean checkBlock(Block block) {
        //校验系统版本是否支持
        if(!Setting.SystemVersionSetting.checkSystemVersion(block.getHeight())){
            LogUtil.debug("系统版本过低，不支持校验区块，请尽快升级系统。");
            return false;
        }

        //校验区块的结构
        if(!StructureTool.checkBlockStructure(block)){
            LogUtil.debug("区块数据异常，请校验区块的结构。");
            return false;
        }
        //校验区块的大小
        if(!SizeTool.checkBlockSize(block)){
            LogUtil.debug("区块数据异常，请校验区块的大小。");
            return false;
        }
        //校验区块写入的属性值
        if(!WritePropertyTool.checkBlockWriteProperties(block)){
            LogUtil.debug("区块校验失败：区块的属性写入值与实际计算结果不一致。");
            return false;
        }


        //校验业务
        Block previousBlock = queryTailBlock();
        //校验区块高度的连贯性
        if(!BlockTool.checkBlockHeight(previousBlock,block)){
            LogUtil.debug("区块写入的区块高度出错。");
            return false;
        }
        //校验区块的前区块哈希
        if(!BlockTool.checkPreviousBlockHash(previousBlock,block)){
            LogUtil.debug("区块写入的前区块哈希出错。");
            return false;
        }
        //校验区块时间
        if(!BlockTool.checkBlockTimestamp(previousBlock,block)){
            LogUtil.debug("区块生成的时间太滞后。");
            return false;
        }
        //校验新产生的哈希
        if(!checkNewHash(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验新产生的地址
        if(!checkNewAddress(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验双花
        if(!checkDoubleSpend(block)){
            LogUtil.debug("区块数据异常，检测到双花攻击。");
            return false;
        }
        //校验共识
        if(!consensus.checkConsensus(this,block)){
            LogUtil.debug("区块数据异常，未满足共识规则。");
            return false;
        }
        //校验激励
        if(!incentive.checkIncentive(this,block)){
            LogUtil.debug("区块数据异常，激励校验失败。");
            return false;
        }

        //从交易角度校验每一笔交易
        for(Transaction transaction : block.getTransactions()){
            boolean transactionCanAddToNextBlock = checkTransaction(transaction);
            if(!transactionCanAddToNextBlock){
                LogUtil.debug("区块数据异常，交易异常。");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkTransaction(Transaction transaction) {
        //校验交易的结构
        if(!StructureTool.checkTransactionStructure(transaction)){
            LogUtil.debug("交易数据异常，请校验交易的结构。");
            return false;
        }
        //校验交易的大小
        if(!SizeTool.checkTransactionSize(transaction)){
            LogUtil.debug("交易数据异常，请校验交易的大小。");
            return false;
        }
        //校验交易的属性是否与计算得来的一致
        if(!WritePropertyTool.checkTransactionWriteProperties(transaction)){
            return false;
        }

        //校验交易中的地址是否是P2PKH地址
        if(!TransactionTool.checkPayToPublicKeyHashAddress(transaction)){
            return false;
        }
        //校验交易中的脚本是否是P2PKH脚本
        if(!TransactionTool.checkPayToPublicKeyHashScript(transaction)){
            return false;
        }

        //业务校验
        //校验新产生的哈希
        if(!checkNewHash(transaction)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验新产生的地址
        if(!checkNewAddress(transaction)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希异常。");
            return false;
        }
        //校验交易金额
        if(!TransactionTool.checkTransactionValue(transaction)){
            LogUtil.debug("交易金额不合法");
            return false;
        }
        //校验双花
        if(!checkDoubleSpend(transaction)){
            LogUtil.debug("交易数据异常，检测到双花攻击。");
            return false;
        }
        //校验用户花费的是自己的钱吗
        if(!TransactionTool.checkUtxoOwnership(transaction)) {
            LogUtil.debug("交易校验失败：交易[输入脚本]解锁交易[输出脚本]异常。");
            return false;
        }
        return true;
    }
    //endregion



    //region 普通查询
    @Override
    public long queryBlockchainHeight() {
        byte[] bytesBlockchainHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainHeightKey());
        if(bytesBlockchainHeight == null){
            return Setting.GenesisBlockSetting.HEIGHT;
        }
        return ByteUtil.byte8ToLong8(bytesBlockchainHeight);
    }

    @Override
    public long queryBlockchainTransactionHeight() {
        byte[] byteTotalTransactionCount = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey());
        if(byteTotalTransactionCount == null){
            return 0;
        }
        return ByteUtil.byte8ToLong8(byteTotalTransactionCount);
    }
    @Override
    public long queryBlockchainTransactionOutputHeight() {
        byte[] byteTotalTransactionCount = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockchainTransactionOutputHeightKey());
        if(byteTotalTransactionCount == null){
            return 0;
        }
        return ByteUtil.byte8ToLong8(byteTotalTransactionCount);
    }
    //endregion



    //region 区块查询
    @Override
    public Block queryTailBlock() {
        long blockchainHeight = queryBlockchainHeight();
        if(LongUtil.isLessEqualThan(blockchainHeight, Setting.GenesisBlockSetting.HEIGHT)){
            return null;
        }
        return queryBlockByBlockHeight(blockchainHeight);
    }
    @Override
    public Block queryBlockByBlockHeight(long blockHeight) {
        byte[] bytesBlock = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(blockHeight));
        if(bytesBlock==null){
            return null;
        }
        return EncodeDecodeTool.decodeToBlock(bytesBlock);
    }
    @Override
    public Block queryBlockByBlockHash(String blockHash) {
        byte[] bytesBlockHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(blockHash));
        if(bytesBlockHeight == null){
            return null;
        }
        return queryBlockByBlockHeight(ByteUtil.byte8ToLong8(bytesBlockHeight));
    }
    //endregion



    //region 交易查询
    @Override
    public Transaction queryTransactionByTransactionHash(String transactionHash) {
        byte[] transactionHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transactionHash));
        if(transactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.byte8ToLong8(transactionHeight));
    }

    @Override
    public Transaction querySourceTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] sourceTransactionHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToSourceTransactionHeightKey(transactionHash,transactionOutputIndex));
        if(sourceTransactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.byte8ToLong8(sourceTransactionHeight));
    }

    @Override
    public Transaction queryDestinationTransactionByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] destinationTransactionHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToDestinationTransactionHeightKey(transactionHash,transactionOutputIndex));
        if(destinationTransactionHeight == null){
            return null;
        }
        return queryTransactionByTransactionHeight(ByteUtil.byte8ToLong8(destinationTransactionHeight));
    }

    @Override
    public TransactionOutput queryTransactionOutputByTransactionOutputHeight(long transactionOutputHeight) {
        byte[] bytesTransactionOutput = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputHeightToTransactionOutputKey(transactionOutputHeight));
        if(bytesTransactionOutput == null){
            return null;
        }
        return EncodeDecodeTool.decodeToTransactionOutput(bytesTransactionOutput);
    }

    @Override
    public Transaction queryTransactionByTransactionHeight(long transactionHeight) {
        byte[] byteTransaction = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transactionHeight));
        if(byteTransaction == null){
            return null;
        }
        return EncodeDecodeTool.decodeToTransaction(byteTransaction);
    }
    //endregion



    //region 交易输出查询
    @Override
    public TransactionOutput queryTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToTransactionOutputHeightKey(transactionHash,transactionOutputIndex));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.byte8ToLong8(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryUnspentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(transactionHash,transactionOutputIndex));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.byte8ToLong8(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput querySpentTransactionOutputByTransactionOutputId(String transactionHash,long transactionOutputIndex) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(transactionHash,transactionOutputIndex));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.byte8ToLong8(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressToTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.byte8ToLong8(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput queryUnspentTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.byte8ToLong8(bytesTransactionOutputHeight));
    }

    @Override
    public TransactionOutput querySpentTransactionOutputByAddress(String address) {
        byte[] bytesTransactionOutputHeight = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressToSpentTransactionOutputHeightKey(address));
        if(bytesTransactionOutputHeight == null){
            return null;
        }
        return queryTransactionOutputByTransactionOutputHeight(ByteUtil.byte8ToLong8(bytesTransactionOutputHeight));
    }
    //endregion



    //region 拼装WriteBatch
    /**
     * 根据区块信息组装WriteBatch对象
     */
    private KvDbUtil.KvWriteBatch createBlockWriteBatch(Block block, BlockchainActionEnum blockchainActionEnum) {
        fillBlockProperty(block);
        KvDbUtil.KvWriteBatch kvWriteBatch = new KvDbUtil.KvWriteBatch();

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
        long transactionIndex = 0;
        long transactionHeight = queryBlockchainTransactionHeight();
        long transactionOutputHeight = queryBlockchainTransactionOutputHeight();
        long blockHeight = block.getHeight();
        String blockHash = block.getHash();
        List<Transaction> transactions = block.getTransactions();
        long transactionCount = BlockTool.getTransactionCount(block);
        block.setTransactionCount(transactionCount);
        block.setPreviousTransactionHeight(transactionHeight);
        if(transactions != null){
            for(Transaction transaction:transactions){
                transactionIndex++;
                transactionHeight++;
                transaction.setBlockHeight(blockHeight);
                transaction.setTransactionIndex(transactionIndex);
                transaction.setTransactionHeight(transactionHeight);

                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for (int i=0; i <outputs.size(); i++){
                        transactionOutputHeight++;
                        TransactionOutput output = outputs.get(i);
                        output.setBlockHeight(blockHeight);
                        output.setBlockHash(blockHash);
                        output.setTransactionHeight(transactionHeight);
                        output.setTransactionHash(transaction.getTransactionHash());
                        output.setTransactionOutputIndex(i+1);
                        output.setTransactionIndex(transaction.getTransactionIndex());
                        output.setTransactionOutputHeight(transactionOutputHeight);
                    }
                }
            }
        }
    }
    /**
     * [交易输出ID]到[来源交易高度]的映射
     */
    private void storeTransactionOutputIdToSourceTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput transactionOutput:outputs){
                        byte[] transactionOutputIdToToSourceTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSourceTransactionHeightKey(transactionOutput.getTransactionHash(),transactionOutput.getTransactionOutputIndex());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToToSourceTransactionHeightKey, ByteUtil.long8ToByte8(transaction.getTransactionHeight()));
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
    private void storeTransactionOutputIdToDestinationTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToToDestinationTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToDestinationTransactionHeightKey(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToToDestinationTransactionHeightKey, ByteUtil.long8ToByte8(transaction.getTransactionHeight()));
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
    private void storeTransactionOutputIdToTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToTransactionOutputHeightKey(output.getTransactionHash(),output.getTransactionOutputIndex());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToTransactionOutputHeightKey, ByteUtil.long8ToByte8(output.getTransactionOutputHeight()));
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
    private void storeTransactionOutputHeightToTransactionOutput(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputHeightToTransactionOutputKey = BlockchainDatabaseKeyTool.buildTransactionOutputHeightToTransactionOutputKey(output.getTransactionOutputHeight());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputHeightToTransactionOutputKey, EncodeDecodeTool.encodeTransactionOutput(output));
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
    private void storeTransactionOutputIdToUnspentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.delete(transactionOutputIdToUnspentTransactionOutputHeightKey);
                        } else {
                            kvWriteBatch.put(transactionOutputIdToUnspentTransactionOutputHeightKey, ByteUtil.long8ToByte8(unspentTransactionOutput.getTransactionOutputHeight()));
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToUnspentTransactionOutputHeightKey(output.getTransactionHash(),output.getTransactionOutputIndex());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToUnspentTransactionOutputHeightKey, ByteUtil.long8ToByte8(output.getTransactionOutputHeight()));
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
    private void storeTransactionOutputIdToSpentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                List<TransactionInput> inputs = transaction.getInputs();
                if(inputs != null){
                    for(TransactionInput transactionInput:inputs){
                        TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                        byte[] transactionOutputIdToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.put(transactionOutputIdToSpentTransactionOutputHeightKey, ByteUtil.long8ToByte8(unspentTransactionOutput.getTransactionOutputHeight()));
                        } else {
                            kvWriteBatch.delete(transactionOutputIdToSpentTransactionOutputHeightKey);
                        }
                    }
                }
                List<TransactionOutput> outputs = transaction.getOutputs();
                if(outputs != null){
                    for(TransactionOutput output:outputs){
                        byte[] transactionOutputIdToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildTransactionOutputIdToSpentTransactionOutputHeightKey(output.getTransactionHash(),output.getTransactionOutputIndex());
                        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                            kvWriteBatch.delete(transactionOutputIdToSpentTransactionOutputHeightKey);
                        } else {
                            kvWriteBatch.put(transactionOutputIdToSpentTransactionOutputHeightKey, ByteUtil.long8ToByte8(output.getTransactionOutputHeight()));
                        }
                    }
                }
            }
        }
    }
    /**
     * 存储交易高度到交易的映射
     */
    private void storeTransactionHeightToTransaction(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                //更新区块链中的交易序列号数据
                byte[] transactionHeightToTransactionKey = BlockchainDatabaseKeyTool.buildTransactionHeightToTransactionKey(transaction.getTransactionHeight());
                if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                    kvWriteBatch.put(transactionHeightToTransactionKey, EncodeDecodeTool.encodeTransaction(transaction));
                } else {
                    kvWriteBatch.delete(transactionHeightToTransactionKey);
                }
            }
        }
    }
    /**
     * 存储交易哈希到交易高度的映射
     */
    private void storeTransactionHashToTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                byte[] transactionHashToTransactionHeightKey = BlockchainDatabaseKeyTool.buildTransactionHashToTransactionHeightKey(transaction.getTransactionHash());
                if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
                    kvWriteBatch.put(transactionHashToTransactionHeightKey, ByteUtil.long8ToByte8(transaction.getTransactionHeight()));
                } else {
                    kvWriteBatch.delete(transactionHashToTransactionHeightKey);
                }
            }
        }
    }
    /**
     * 存储区块链的高度
     */
    private void storeBlockchainHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockchainHeightKey = BlockchainDatabaseKeyTool.buildBlockchainHeightKey();
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.long8ToByte8(block.getHeight()));
        }else{
            kvWriteBatch.put(blockchainHeightKey, ByteUtil.long8ToByte8(block.getHeight()-1));
        }
    }
    /**
     * 存储区块哈希到区块高度的映射
     */
    private void storeBlockHashToBlockHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockHashBlockHeightKey = BlockchainDatabaseKeyTool.buildBlockHashToBlockHeightKey(block.getHash());
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockHashBlockHeightKey, ByteUtil.long8ToByte8(block.getHeight()));
        }else{
            kvWriteBatch.delete(blockHashBlockHeightKey);
        }
    }
    /**
     * 存储区块链中总的交易高度
     */
    private void storeBlockchainTransactionHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        long transactionCount = queryBlockchainTransactionHeight();
        byte[] bytesBlockchainTransactionCountKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionHeightKey();
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.long8ToByte8(transactionCount + BlockTool.getTransactionCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionCountKey, ByteUtil.long8ToByte8(transactionCount - BlockTool.getTransactionCount(block)));
        }
    }
    /**
     * 存储区块链中总的交易数量
     */
    private void storeBlockchainTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        long transactionOutputCount = queryBlockchainTransactionOutputHeight();
        byte[] bytesBlockchainTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildBlockchainTransactionOutputHeightKey();
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.long8ToByte8(transactionOutputCount + BlockTool.getTransactionOutputCount(block)));
        }else{
            kvWriteBatch.put(bytesBlockchainTransactionOutputHeightKey, ByteUtil.long8ToByte8(transactionOutputCount - BlockTool.getTransactionOutputCount(block)));
        }
    }
    /**
     * 存储区块链高度到区块的映射
     */
    private void storeBlockHeightToBlock(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockHeightKey = BlockchainDatabaseKeyTool.buildBlockHeightToBlockKey(block.getHeight());
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockHeightKey, EncodeDecodeTool.encodeBlock(block));
        }else{
            kvWriteBatch.delete(blockHeightKey);
        }
    }

    /**
     * 存储已使用的哈希
     */
    private void storeHash(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        byte[] blockHashKey = BlockchainDatabaseKeyTool.buildHashKey(block.getHash());
        if(BlockchainActionEnum.ADD_BLOCK == blockchainActionEnum){
            kvWriteBatch.put(blockHashKey, blockHashKey);
        } else {
            kvWriteBatch.delete(blockHashKey);
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
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
    private void storeAddress(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
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
    private void storeAddressToUnspentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null){
            return;
        }
        for(Transaction transaction : transactions){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspentTransactionOutput();
                    byte[] addressToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(utxo.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.delete(addressToUnspentTransactionOutputHeightKey);
                    }else{
                        kvWriteBatch.put(addressToUnspentTransactionOutputHeightKey, ByteUtil.long8ToByte8(utxo.getTransactionOutputHeight()));
                    }
                }
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (TransactionOutput transactionOutput:outputs){
                    byte[] addressToUnspentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToUnspentTransactionOutputHeightKey(transactionOutput.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.put(addressToUnspentTransactionOutputHeightKey, ByteUtil.long8ToByte8(transactionOutput.getTransactionOutputHeight()));
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
    private void storeAddressToTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null){
            return;
        }
        for(Transaction transaction : transactions){
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                for (TransactionOutput transactionOutput:outputs){
                    byte[] addressToTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToTransactionOutputHeightKey(transactionOutput.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.put(addressToTransactionOutputHeightKey, ByteUtil.long8ToByte8(transactionOutput.getTransactionOutputHeight()));
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
    private void storeAddressToSpentTransactionOutputHeight(KvDbUtil.KvWriteBatch kvWriteBatch, Block block, BlockchainActionEnum blockchainActionEnum) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null){
            return;
        }
        for(Transaction transaction : transactions){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                for (TransactionInput transactionInput:inputs){
                    TransactionOutput utxo = transactionInput.getUnspentTransactionOutput();
                    byte[] addressToSpentTransactionOutputHeightKey = BlockchainDatabaseKeyTool.buildAddressToSpentTransactionOutputHeightKey(utxo.getAddress());
                    if(blockchainActionEnum == BlockchainActionEnum.ADD_BLOCK){
                        kvWriteBatch.put(addressToSpentTransactionOutputHeightKey, ByteUtil.long8ToByte8(utxo.getTransactionOutputHeight()));
                    }else{
                        kvWriteBatch.delete(addressToSpentTransactionOutputHeightKey);
                    }
                }
            }
        }
    }
    //endregion


    /**
     * 检查[花费的交易输出]是否都是[未花费的交易输出]
     */
    private boolean checkStxoIsUtxo(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                TransactionOutput transactionOutput = queryUnspentTransactionOutputByTransactionOutputId(unspentTransactionOutput.getTransactionHash(),unspentTransactionOutput.getTransactionOutputIndex());
                if(transactionOutput == null){
                    LogUtil.debug("交易数据异常：交易输入不是未花费交易输出。");
                    return false;
                }
            }
        }
        return true;
    }

    private String getBlockchainDatabasePath(){
        return FileUtil.newPath(coreConfiguration.getCorePath(), BLOCKCHAIN_DATABASE_NAME);
    }

    //region 新产生的哈希相关
    /**
     * 哈希是否已经被区块链系统使用了？
     */
    private boolean isHashUsed(String hash){
        byte[] bytesHash = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildHashKey(hash));
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
     * 区块中校验新产生的哈希
     */
    private boolean checkNewHash(Transaction transaction) {
        //校验哈希作为主键的正确性
        //新产生的Hash不能被使用过
        if(isNewHashUsed(transaction)){
            LogUtil.debug("校验数据异常，校验中占用的部分主键已经被使用了。");
            return false;
        }
        return true;
    }
    /**
     * 校验区块新产生的哈希
     */
    private boolean checkNewHash(Block block) {
        //校验哈希作为主键的正确性
        //新产生的哈希不能有重复
        if(BlockTool.isExistDuplicateNewHash(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希有重复。");
            return false;
        }
        //新产生的哈希不能被区块链使用过了
        if(isHashUsed(block)){
            LogUtil.debug("区块数据异常，区块中新产生的哈希已经早被区块链使用了。");
            return false;
        }
        return true;
    }
    /**
     * 校验区块新产生的地址
     */
    private boolean checkNewAddress(Block block) {
        //校验地址作为主键的正确性
        //新产生的地址不能有重复
        if(BlockTool.isExistDuplicateNewAddress(block)){
            LogUtil.debug("区块数据异常，区块中新产生的地址有重复。");
            return false;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!checkNewAddress(transaction)){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 区块中校验新产生的哈希
     */
    private boolean checkNewAddress(Transaction transaction) {
        //区块新产生的地址不能有重复
        if(TransactionTool.isExistDuplicateNewAddress(transaction)){
            return false;
        }
        //区块新产生的地址不能被使用过了
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs){
                String address = output.getAddress();
                if(isAddressUsed(address)){
                    LogUtil.debug(String.format("区块数据异常，地址[%s]重复。",address));
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isAddressUsed(String address) {
        byte[] bytesAddress = KvDbUtil.get(getBlockchainDatabasePath(), BlockchainDatabaseKeyTool.buildAddressKey(address));
        return bytesAddress != null;
    }
    //endregion


    //region 双花攻击
    /**
     * 校验双花
     */
    private boolean checkDoubleSpend(Transaction transaction) {
        //双花交易：交易内部存在重复的[未花费交易输出]
        if(TransactionTool.isExistDuplicateUtxo(transaction)){
            LogUtil.debug("交易数据异常，检测到双花攻击。");
            return false;
        }
        //双花交易：交易内部使用了[已经花费的[未花费交易输出]]
        if(!checkStxoIsUtxo(transaction)){
            LogUtil.debug("交易数据异常：发生双花交易。");
            return false;
        }
        return true;
    }

    /**
     * 校验双花
     * 双花指的是同一笔UTXO被花费两次或多次。
     */
    private boolean checkDoubleSpend(Block block) {
        //双花交易：区块内部存在重复的[未花费交易输出]
        if(BlockTool.isExistDuplicateUtxo(block)){
            LogUtil.debug("区块数据异常：发生双花交易。");
            return false;
        }
        List<Transaction> transactions = block.getTransactions();
        if(transactions != null){
            for(Transaction transaction:transactions){
                if(!checkDoubleSpend(transaction)){
                    LogUtil.debug("区块数据异常：发生双花交易。");
                    return false;
                }
            }
        }
        return true;
    }
    //endregion
}