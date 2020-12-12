package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * (区块、交易)结构大小工具类
 *
 * 存储大小是基于DTO对象计算的。考虑到多种语言实现区块链，若采用不同语言实现所构造的model计算大小，可能比较复杂。
 * 而DTO本身能组成区块链的完整数据，DTO数据又比较精简，所以基于DTO计算区块大小、交易大小非常方便。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class StructureSizeTool {

    private static final Logger logger = LoggerFactory.getLogger(StructureSizeTool.class);

    //region 校验存储容量
    /**
     * 校验区块的存储容量是否合法：用来限制区块所占存储空间的大小。
     */
    public static boolean isBlockStorageCapacityLegal(Block block) {
        return isBlockStorageCapacityLegal(Model2DtoTool.block2BlockDTO(block));
    }
    public static boolean isBlockStorageCapacityLegal(BlockDTO blockDTO) {
        //区块的时间戳的长度不需要校验  假设时间戳长度不正确，则在随后的业务逻辑中走不通

        //区块的前哈希的长度不需要校验  假设前哈希长度不正确，则在随后的业务逻辑中走不通

        //校验共识占用存储空间
        long nonceByteSize = calculateNonceByteSize(blockDTO.getNonce());
        if(!LongUtil.isEquals(nonceByteSize, GlobalSetting.BlockConstant.NONCE_TEXT_SIZE)){
            logger.debug(String.format("nonce[%s]长度不是[%s]。",blockDTO.getNonce(),GlobalSetting.BlockConstant.NONCE_TEXT_SIZE));
            return false;
        }

        //校验每一笔交易占用的存储空间
        List<TransactionDTO> transactionDtoList = blockDTO.getTransactionDtoList();
        if(transactionDtoList != null){
            for(TransactionDTO transactionDTO:transactionDtoList){
                if(!isTransactionStorageCapacityLegal(transactionDTO)){
                    logger.debug("交易数据异常，交易的容量非法。");
                    return false;
                }
            }
        }

        //校验区块占用的存储空间
        long blockByteSize = calculateBlockByteSize(blockDTO);
        if(blockByteSize > GlobalSetting.BlockConstant.BLOCK_TEXT_MAX_SIZE){
            logger.debug(String.format("区块数据大小[%s]超过限制[%s]。",blockByteSize,GlobalSetting.BlockConstant.BLOCK_TEXT_MAX_SIZE));
            return false;
        }
        return true;
    }
    /**
     * 校验交易的存储容量是否合法：用来限制交易的所占存储空间的大小。
     */
    public static boolean isTransactionStorageCapacityLegal(Transaction transaction) {
        return isTransactionStorageCapacityLegal(Model2DtoTool.transaction2TransactionDTO(transaction));
    }
    public static boolean isTransactionStorageCapacityLegal(TransactionDTO transactionDTO) {
        //校验交易输入
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getTransactionInputDtoList();
        if(transactionInputDtoList != null){
            for(TransactionInputDTO transactionInputDTO:transactionInputDtoList){
                //交易的未花费输出所占存储容量不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本存储容量
                InputScriptDTO inputScriptDTO = transactionInputDTO.getInputScriptDTO();
                //校验脚本操作码操作数的容量
                if(!isScriptStorageCapacityLegal(inputScriptDTO)){
                    return false;
                }
            }
        }

        //校验交易输出
        List<TransactionOutputDTO> transactionOutputDtoList = transactionDTO.getTransactionOutputDtoList();
        if(transactionOutputDtoList != null){
            for(TransactionOutputDTO transactionOutputDTO:transactionOutputDtoList){
                //交易金额所占存储容量不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本存储容量
                OutputScriptDTO outputScriptDTO = transactionOutputDTO.getOutputScriptDTO();
                //校验脚本操作码操作数的容量
                if(!isScriptStorageCapacityLegal(outputScriptDTO)){
                    return false;
                }

            }
        }

        //校验整笔交易所占存储空间
        long transactionByteSize = calculateTransactionByteSize(transactionDTO);
        if(calculateTransactionByteSize(transactionDTO) > GlobalSetting.BlockConstant.TRANSACTION_TEXT_MAX_SIZE){
            logger.debug(String.format("交易数据大小[%s]超过存储容量限制[%s]。",transactionByteSize,GlobalSetting.BlockConstant.TRANSACTION_TEXT_MAX_SIZE));
            return false;
        }
        return true;
    }
    /**
     * 校验脚本操作码、操作数的存储容量
     * 校验脚本的存储容量
     */
    public static boolean isScriptStorageCapacityLegal(ScriptDTO scriptDTO) {
        for(int i=0;i<scriptDTO.size();i++){
            String operationCode = scriptDTO.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                continue;
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA1024.getCode(),bytesOperationCode)){
                String operationData = scriptDTO.get(++i);
                if(operationData.length()/2 > OperationCodeEnum.OP_PUSHDATA1024.getSize()){
                    return false;
                }
            }else {
                return false;
            }
        }
        if(calculateScriptByteSize(scriptDTO) > GlobalSetting.ScriptConstant.SCRIPT_TEXT_MAX_SIZE){
            logger.debug("交易校验失败：交易输出脚本所占存储空间超出限制。");
            return false;
        }
        return true;
    }
    //endregion



    //region 计算文本大小
    public static long calculateBlockByteSize(Block block) {
        return calculateBlockByteSize(Model2DtoTool.block2BlockDTO(block));
    }
    public static long calculateBlockByteSize(BlockDTO blockDTO) {
        long size = 0;
        long timestamp = blockDTO.getTimestamp();
        size += calculateTimestampByteSize(timestamp);

        String previousBlockHash = blockDTO.getPreviousBlockHash();
        size += calculateHashByteSize(previousBlockHash);

        String nonce = blockDTO.getNonce();
        size += calculateNonceByteSize(nonce);

        List<TransactionDTO> transactionDtoList = blockDTO.getTransactionDtoList();
        for(TransactionDTO transactionDTO:transactionDtoList){
            size += calculateTransactionByteSize(transactionDTO);
        }
        return size;
    }
    private static long calculateHashByteSize(String hash) {
        return hash.length()/2;
    }
    private static long calculateNonceByteSize(String nonce) {
        return nonce.length()/2;
    }
    public static long calculateTransactionByteSize(TransactionDTO transactionDTO) {
        long size = 0;
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getTransactionInputDtoList();
        size += calculateTransactionInputByteSize(transactionInputDtoList);
        List<TransactionOutputDTO> transactionOutputDtoList = transactionDTO.getTransactionOutputDtoList();
        size += calculateTransactionOutputByteSize(transactionOutputDtoList);
        return size;
    }
    private static long calculateTransactionOutputByteSize(List<TransactionOutputDTO> transactionOutputDtoList) {
        long size = 0;
        if(transactionOutputDtoList == null || transactionOutputDtoList.size()==0){
            return size;
        }
        for(TransactionOutputDTO transactionOutputDTO:transactionOutputDtoList){
            size += calculateTransactionOutputByteSize(transactionOutputDTO);
        }
        return size;
    }
    private static long calculateTransactionOutputByteSize(TransactionOutputDTO transactionOutputDTO) {
        long size = 0;
        OutputScriptDTO outputScriptDTO = transactionOutputDTO.getOutputScriptDTO();
        size += calculateScriptByteSize(outputScriptDTO);
        long value = transactionOutputDTO.getValue();
        size += calculateValueByteSize(value);
        return size;
    }
    private static long calculateTransactionInputByteSize(List<TransactionInputDTO> inputs) {
        long size = 0;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInputDTO transactionInputDTO:inputs){
            size += calculateTransactionInputByteSize(transactionInputDTO);
        }
        return size;
    }
    private static long calculateTransactionInputByteSize(TransactionInputDTO input) {
        long size = 0;
        UnspendTransactionOutputDTO unspendTransactionOutputDTO = input.getUnspendTransactionOutputDTO();
        size += calculateTransactionOutputByteSize(unspendTransactionOutputDTO);
        InputScriptDTO inputScriptDTO = input.getInputScriptDTO();
        size += calculateScriptByteSize(inputScriptDTO);
        return size;
    }
    private static long calculateTransactionOutputByteSize(UnspendTransactionOutputDTO unspendTransactionOutputDTO) {
        long size = 0;
        String transactionHash = unspendTransactionOutputDTO.getTransactionHash();
        size += calculateHashByteSize(transactionHash);
        long transactionOutputIndex = unspendTransactionOutputDTO.getTransactionOutputIndex();
        size += calculateTransactionOutputIndexByteSize(transactionOutputIndex);
        return size;
    }
    private static long calculateScriptByteSize(ScriptDTO script) {
        long size = 0;
        if(script == null || script.size()==0){
            return size;
        }
        for(String scriptCode:script){
            size += scriptCode.length()/2;
        }
        return size;
    }
    private static long calculateValueByteSize(long value){
        return GlobalSetting.BlockConstant.NUMBER_TEXT_SIZE;
    }
    private static long calculateTimestampByteSize(long timestamp) {
        return GlobalSetting.BlockConstant.NUMBER_TEXT_SIZE;
    }
    private static long calculateTransactionOutputIndexByteSize(long transactionOutputIndex) {
        return GlobalSetting.BlockConstant.NUMBER_TEXT_SIZE;
    }
    //endregion


    
    //region 校验结构
    /**
     * 校验区块的结构
     */
    public static boolean isBlockStructureLegal(Block block) {
        List<Transaction> transactions = block.getTransactions();
        if(transactions == null || transactions.size()==0){
            logger.debug("区块数据异常：区块中的交易数量为0。区块必须有一笔CoinBase的交易。");
            return false;
        }
        //校验区块中交易的数量
        long transactionCount = BlockTool.getTransactionCount(block);
        if(transactionCount > GlobalSetting.BlockConstant.BLOCK_MAX_TRANSACTION_COUNT){
            logger.debug(String.format("区块包含交易数量是[%s]超过限制[%s]。",transactionCount,GlobalSetting.BlockConstant.BLOCK_MAX_TRANSACTION_COUNT));
            return false;
        }
        for(int i=0; i<transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            if(i == 0){
                if(transaction.getTransactionType() != TransactionType.COINBASE){
                    logger.debug("区块数据异常：区块第一笔交易必须是CoinBase。");
                    return false;
                }
            }else {
                if(transaction.getTransactionType() != TransactionType.NORMAL){
                    logger.debug("区块数据异常：区块非第一笔交易必须是普通交易。");
                    return false;
                }
            }
        }
        //校验交易的结构
        for(Transaction transaction:transactions){
            if(!isTransactionStructureLegal(transaction)){
                logger.debug("交易数据异常：交易结构异常。");
                return false;
            }
        }
        return true;
    }
    /**
     * 校验交易的结构
     */
    public static boolean isTransactionStructureLegal(Transaction transaction) {
        TransactionType transactionType = transaction.getTransactionType();
        if(TransactionType.COINBASE == transactionType){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                logger.debug("交易数据异常：CoinBase交易不能有交易输入。");
                return false;
            }
            List<TransactionOutput> outputs = transaction.getOutputs();
            if(outputs == null || outputs.size()!=1){
                logger.debug("交易数据异常：CoinBase交易有且只能有一笔交易输出。");
                return false;
            }
            return true;
        }else if(TransactionType.NORMAL == transactionType){
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs == null || inputs.size()<1){
                logger.debug("交易数据异常：普通交易的交易输入数量至少是1。");
                return false;
            }
            return true;
        }else {
            logger.debug("交易数据异常：不能识别的交易的类型。");
            return false;
        }
    }
    //endregion
}
