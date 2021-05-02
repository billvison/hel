package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.UnconfirmedTransactionDatabase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 矿工工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class MinerTool {

    /**
     * 构建挖矿中的区块对象，该区块的nonce为空。
     */
    public static Block buildMiningBlock(BlockchainDatabase blockchainDataBase, UnconfirmedTransactionDatabase unconfirmedTransactionDataBase, Account minerAccount) {
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
                    LogUtil.error("类型转换异常,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash,e);
                    unconfirmedTransactionDataBase.deleteByTransactionHash(transactionHash);
                }
            }
        }

        backupTransactionList.clear();
        backupTransactionList.addAll(transactionList);
        transactionList.clear();

        for(Transaction transaction : backupTransactionList){
            boolean transactionCanAddToNextBlock = blockchainDataBase.isTransactionCanAddToNextBlock(transaction);
            if(transactionCanAddToNextBlock){
                transactionList.add(transaction);
            }else {
                String transactionHash = TransactionTool.calculateTransactionHash(transaction);
                LogUtil.debug("交易不能被挖矿,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash);
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
                        LogUtil.debug("交易不能被挖矿,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash);
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
                        LogUtil.debug("交易不能被挖矿,将从挖矿交易数据库中删除该交易。交易哈希"+transactionHash);
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
        TransactionTool.sortByFeeRateDescend(transactionList);


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


        Block nextMineBlock = buildMiningBlock(blockchainDataBase,transactionList,minerAccount);
        return nextMineBlock;
    }

    /**
     * 构建挖矿中的区块对象，该区块的nonce为空。
     */
    public static Block buildMiningBlock(BlockchainDatabase blockchainDataBase, List<Transaction> packingTransactionList, Account minerAccount) {
        long timestamp = TimeUtil.currentTimeMillis();

        Block tailBlock = blockchainDataBase.queryTailBlock();
        Block nonNonceBlock = new Block();
        //这个挖矿时间不需要特别精确，没必要非要挖出矿的前一霎那时间。
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
        //激励
        long incentiveValue = blockchainDataBase.getIncentive().incentiveAmount(nonNonceBlock);
        Transaction mineAwardTransaction =  buildIncentiveTransaction(minerAccount.getAddress(),incentiveValue);
        packingTransactionList.add(0,mineAwardTransaction);

        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(nonNonceBlock);
        nonNonceBlock.setMerkleTreeRoot(merkleTreeRoot);
        return nonNonceBlock;
    }

    /**
     * 构建区块的挖矿奖励交易。
     */
    public static Transaction buildIncentiveTransaction(String address,long incentiveValue) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.COINBASE);

        ArrayList<TransactionOutput> outputs = new ArrayList<>();
        TransactionOutput output = new TransactionOutput();
        output.setAddress(address);
        output.setValue(incentiveValue);
        output.setOutputScript(ScriptTool.createPayToPublicKeyHashOutputScript(address));
        outputs.add(output);

        transaction.setOutputs(outputs);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transaction));
        return transaction;
    }
}
