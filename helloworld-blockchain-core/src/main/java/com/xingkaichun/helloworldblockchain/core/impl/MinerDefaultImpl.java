package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.*;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.tools.*;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class MinerDefaultImpl extends Miner {

    private static final Logger logger = LoggerFactory.getLogger(MinerDefaultImpl.class);

    private static final Random RANDOM = new Random();

    //region 属性与构造函数
    //挖矿开关:默认打开挖矿的开关
    private boolean mineOption = true;

    public MinerDefaultImpl(Wallet wallet, BlockchainDatabase blockchainDataBase, UnconfirmedTransactionDatabase unconfirmedTransactionDataBase) {
        super(wallet,blockchainDataBase, unconfirmedTransactionDataBase);
    }
    //endregion


    @Override
    public void start() {
        while(true){
            ThreadUtil.sleep(10);
            if(!mineOption){
                continue;
            }
            Account minerAccount = wallet.createAccount();
            Block block = obtainMiningBlock(minerAccount);
            //随机nonce。好处：不同实例，从不同的nonce开始尝试计算符合要求的nonce。
            byte[] nonceBytes = new byte[32];
            long startTimestamp = System.currentTimeMillis();
            while(true){
                if(!mineOption){
                    break;
                }
                //在挖矿的期间，可能收集到新的交易。每隔一定的时间，重新组装挖矿中的block，组装新的挖矿中的block的时候，可以考虑将新收集到交易放进挖矿中的block。
                if(System.currentTimeMillis()-startTimestamp > GlobalSetting.MinerConstant.MINE_TIMESTAMP_PER_ROUND){
                    break;
                }
                RANDOM.nextBytes(nonceBytes);
                block.setNonce(HexUtil.bytesToHexString(nonceBytes));
                block.setHash(BlockTool.calculateBlockHash(block));
                //挖矿成功
                if(blockchainDataBase.getConsensus().isReachConsensus(blockchainDataBase,block)){
                    //将账户放入钱包
                    wallet.addAccount(minerAccount);
                    logger.info("祝贺您！挖矿成功！！！区块高度:"+block.getHeight()+",区块哈希:"+block.getHash());
                    //将矿放入区块链
                    boolean isAddBlockToBlockchainSuccess = blockchainDataBase.addBlock(block);
                    if(!isAddBlockToBlockchainSuccess){
                        logger.error("挖矿成功，但是放入区块链失败。请检查异常。");
                        continue;
                    }
                    break;
                }
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
    private Block obtainMiningBlock(Account minerAccount) {
        //获取一部分未确认交易，最优的方式是获取所有未确认的交易进行处理，但是数据处理起来会很复杂，因为项目是helloworld的，所以简单的拿一部分数据即可。
        List<TransactionDTO> forMineBlockTransactionDtoList = unconfirmedTransactionDataBase.selectTransactionDtoList(1,10000);

        List<Transaction> transactionList = new ArrayList<>();
        List<Transaction> backupTransactionList = new ArrayList<>();

        if(forMineBlockTransactionDtoList != null){
            for(TransactionDTO transactionDTO:forMineBlockTransactionDtoList){
                try {
                    Transaction transaction = Dto2ModelTool.transactionDto2Transaction(blockchainDataBase,transactionDTO);
                    transactionList.add(transaction);
                } catch (Exception e) {
                    String transactionHash = TransactionTool.calculateTransactionHash(transactionDTO);
                    logger.info("类型转换异常,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash,e);
                    unconfirmedTransactionDataBase.deleteByTransactionHash(transactionHash);
                }
            }
        }

        backupTransactionList.clear();
        backupTransactionList.addAll(transactionList);
        transactionList.clear();

        for(Transaction transaction : backupTransactionList){
            boolean transactionCanAddToNextBlock = blockchainDataBase.isTransactionCanAddToNextBlock(null,transaction);
            if(transactionCanAddToNextBlock){
                transactionList.add(transaction);
            }else {
                String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                logger.info("交易不能被挖矿,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash);
                unconfirmedTransactionDataBase.deleteByTransactionHash(transactionHash);
            }
        }

        backupTransactionList.clear();
        backupTransactionList.addAll(transactionList);
        transactionList.clear();


        //防止双花
        Set<String> transactionOutputIdSet = new HashSet<>();
        for(Transaction transaction : backupTransactionList){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null){
                boolean canAdd = true;
                for(TransactionInput transactionInput : inputs) {
                    TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                    String transactionOutputId = unspentTransactionOutput.getTransactionOutputId();
                    if(transactionOutputIdSet.contains(transactionOutputId)){
                        canAdd = false;
                        String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                        logger.info("交易不能被挖矿,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash);
                        unconfirmedTransactionDataBase.deleteByTransactionHash(transactionHash);
                        break;
                    }else {
                        transactionOutputIdSet.add(transactionOutputId);
                    }
                }
                if(canAdd){
                    transactionList.add(transaction);
                }
            }
        }



        backupTransactionList.clear();
        backupTransactionList.addAll(transactionList);
        transactionList.clear();


        //防止一个地址被用多次
        Set<String> addressSet = new HashSet<>();
        for(Transaction transaction : backupTransactionList){
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs != null){
                boolean canAdd = true;
                for (TransactionOutput output:outputs){
                    String address = output.getAddress();
                    if(addressSet.contains(address)){
                        canAdd = false;
                        String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                        logger.info("交易不能被挖矿,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash);
                        unconfirmedTransactionDataBase.deleteByTransactionHash(transactionHash);
                        break;
                    }else {
                        addressSet.add(address);
                    }
                }
                if(canAdd){
                    transactionList.add(transaction);
                }
            }
        }


        //按照费率(每字符的手续费)从大到小排序交易
        transactionList.sort((transaction1, transaction2) -> {
            long transaction1FeeRate = TransactionTool.getFeeRate(transaction1);
            long transaction2FeeRate = TransactionTool.getFeeRate(transaction2);
            long diffFeeRate = transaction1FeeRate - transaction2FeeRate;
            if(diffFeeRate>0){
                return -1;
            }else if(diffFeeRate==0){
                return 0;
            }else {
                return 1;
            }
        });


        backupTransactionList.clear();
        backupTransactionList.addAll(transactionList);
        transactionList.clear();

        //到此时，剩余交易都是经过验证的了，且按照交易费率从大到小排列了。
        //尽可能多的获取交易
        long size = 0;
        for(int i=0; i<backupTransactionList.size(); i++){
            //序号从0开始，加一。
            //留给挖矿交易一个位置，减一。
            if(i+1 > GlobalSetting.BlockConstant.BLOCK_MAX_TRANSACTION_COUNT-1){
                break;
            }
            Transaction transaction = backupTransactionList.get(i);
            size += SizeTool.calculateTransactionSize(transaction);
            if(size > GlobalSetting.BlockConstant.BLOCK_TEXT_MAX_SIZE){
                break;
            }
            transactionList.add(transaction);
        }


        Block nextMineBlock = buildNextMineBlock(transactionList,minerAccount);
        return nextMineBlock;
    }

    /**
     * 构建区块的挖矿奖励交易，这里可以实现挖矿奖励的分配。
     */
    private Transaction buildMineAwardTransaction(Account minerAccount, Block block) {
        String address = minerAccount.getAddress();

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.COINBASE);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        TransactionOutput output = new TransactionOutput();
        output.setAddress(address);
        output.setValue(blockchainDataBase.getIncentive().incentiveAmount(block));
        output.setOutputScript(ScriptTool.createPayToPublicKeyHashOutputScript(address));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transaction));
        return transaction;
    }

    /**
     * 构建挖矿区块
     */
    private Block buildNextMineBlock(List<Transaction> packingTransactionList, Account minerAcount) {
        long timestamp = System.currentTimeMillis();

        Block tailBlock = blockchainDataBase.queryTailBlock();
        Block nonNonceBlock = new Block();
        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。省去了挖矿时实时更新这个时间的繁琐。
        nonNonceBlock.setTimestamp(timestamp);

        if(tailBlock == null){
            nonNonceBlock.setHeight(GlobalSetting.GenesisBlock.HEIGHT +1);
            nonNonceBlock.setPreviousBlockHash(GlobalSetting.GenesisBlock.HASH);
        } else {
            nonNonceBlock.setHeight(tailBlock.getHeight()+1);
            nonNonceBlock.setPreviousBlockHash(tailBlock.getHash());
        }
        nonNonceBlock.setTransactions(packingTransactionList);

        //创建挖矿奖励交易
        Transaction mineAwardTransaction =  buildMineAwardTransaction(minerAcount,nonNonceBlock);
        packingTransactionList.add(0,mineAwardTransaction);


        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(nonNonceBlock);
        nonNonceBlock.setMerkleTreeRoot(merkleTreeRoot);
        return nonNonceBlock;
    }
}
