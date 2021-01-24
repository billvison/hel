package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * (区块、交易)大小工具类
 *
 * 存储大小是基于DTO对象计算的。考虑到多种语言实现区块链，若采用不同语言实现所构造的model计算大小，可能比较复杂。
 * 而DTO本身能组成区块链的完整数据，DTO数据又比较精简，所以基于DTO计算区块大小、交易大小非常方便。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SizeTool {

    private static final Logger logger = LoggerFactory.getLogger(SizeTool.class);

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
        if(!LongUtil.isEquals(nonceByteSize, GlobalSetting.BlockConstant.NONCE_TEXT_SIZE*2)){
            logger.debug(String.format("nonce[%s]长度不是[%s]。",blockDTO.getNonce(),GlobalSetting.BlockConstant.NONCE_TEXT_SIZE*2));
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
                if(operationData.length() > OperationCodeEnum.OP_PUSHDATA1024.getSize()){
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



    //region 计算大小
    public static long calculateBlockByteSize(Block block) {
        return calculateBlockByteSize(Model2DtoTool.block2BlockDTO(block));
    }
    public static long calculateBlockByteSize(BlockDTO blockDTO) {
        long size = 0;
        long timestamp = blockDTO.getTimestamp();
        size += calculateTimestampByteSize(timestamp);

        String previousBlockHash = blockDTO.getPreviousBlockHash();
        size += calculatePreviousBlockHashByteSize(previousBlockHash);

        String nonce = blockDTO.getNonce();
        size += calculateNonceByteSize(nonce);

        List<TransactionDTO> transactionDtoList = blockDTO.getTransactionDtoList();
        for(TransactionDTO transactionDTO:transactionDtoList){
            size += calculateTransactionByteSize(transactionDTO);
        }
        return size;
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
        size += calculateTransactionHashByteSize(transactionHash);
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
            size += scriptCode.length();
        }
        return size;
    }


    private static long calculatePreviousBlockHashByteSize(String previousBlockHash) {
        return previousBlockHash.length();
    }
    private static long calculateTransactionHashByteSize(String transactionHash) {
        return transactionHash.length();
    }
    private static long calculateNonceByteSize(String nonce) {
        return nonce.length();
    }
    private static long calculateValueByteSize(long value){
        return String.valueOf(value).length();
    }
    private static long calculateTimestampByteSize(long timestamp) {
        return String.valueOf(timestamp).length();
    }
    private static long calculateTransactionOutputIndexByteSize(long transactionOutputIndex) {
        return String.valueOf(transactionOutputIndex).length();
    }
    //endregion
}
