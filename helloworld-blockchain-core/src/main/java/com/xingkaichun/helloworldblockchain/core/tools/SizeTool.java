package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;

import java.util.Arrays;
import java.util.List;

/**
 * (区块、交易)大小工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class SizeTool {

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

        //校验区块随机数占用存储空间
        long nonceByteSize = stringSize(blockDTO.getNonce());
        if(!LongUtil.isEquals(nonceByteSize, GlobalSetting.BlockConstant.NONCE_TEXT_SIZE)){
            LogUtil.debug(String.format("nonce[%s]长度不是[%s]。",blockDTO.getNonce(),GlobalSetting.BlockConstant.NONCE_TEXT_SIZE));
            return false;
        }

        //校验每一笔交易占用的存储空间
        List<TransactionDTO> transactionDtoList = blockDTO.getTransactions();
        if(transactionDtoList != null){
            for(TransactionDTO transactionDTO:transactionDtoList){
                if(!isTransactionStorageCapacityLegal(transactionDTO)){
                    LogUtil.debug("交易数据异常，交易的容量非法。");
                    return false;
                }
            }
        }

        //校验区块占用的存储空间
        long blockByteSize = calculateBlockSize(blockDTO);
        if(blockByteSize > GlobalSetting.BlockConstant.BLOCK_TEXT_MAX_SIZE){
            LogUtil.debug(String.format("区块数据大小[%s]超过限制[%s]。",blockByteSize,GlobalSetting.BlockConstant.BLOCK_TEXT_MAX_SIZE));
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
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getInputs();
        if(transactionInputDtoList != null){
            for(TransactionInputDTO transactionInputDTO:transactionInputDtoList){
                //交易的未花费输出所占存储容量不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本存储容量
                InputScriptDTO inputScriptDTO = transactionInputDTO.getInputScript();
                //校验脚本操作码操作数的容量
                if(!isInputScriptStorageCapacityLegal(inputScriptDTO)){
                    return false;
                }
            }
        }

        //校验交易输出
        List<TransactionOutputDTO> transactionOutputDtoList = transactionDTO.getOutputs();
        if(transactionOutputDtoList != null){
            for(TransactionOutputDTO transactionOutputDTO:transactionOutputDtoList){
                //交易金额所占存储容量不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本存储容量
                OutputScriptDTO outputScriptDTO = transactionOutputDTO.getOutputScript();
                //校验脚本操作码操作数的容量
                if(!isOutputScriptCapacityLegal(outputScriptDTO)){
                    return false;
                }

            }
        }

        //校验整笔交易所占存储空间
        long transactionByteSize = calculateTransactionSize(transactionDTO);
        if(calculateTransactionSize(transactionDTO) > GlobalSetting.TransactionConstant.TRANSACTION_TEXT_MAX_SIZE){
            LogUtil.debug(String.format("交易数据大小[%s]超过存储容量限制[%s]。",transactionByteSize,GlobalSetting.TransactionConstant.TRANSACTION_TEXT_MAX_SIZE));
            return false;
        }
        return true;
    }

    /**
     * 校验脚本操作码、操作数的存储容量
     * 校验脚本的存储容量
     */
    private static boolean isInputScriptStorageCapacityLegal(InputScriptDTO inputScriptDTO) {
        //先宽泛的校验脚本
        if(!isScriptStorageCapacityLegal(inputScriptDTO)){
            return false;
        }
        return ScriptTool.isPayToPublicKeyHashInputScript(inputScriptDTO);
    }

    /**
     * 校验脚本操作码、操作数的存储容量
     * 校验脚本的存储容量
     */
    public static boolean isOutputScriptCapacityLegal(OutputScriptDTO outputScriptDTO) {
        //先宽泛的校验脚本
        if(!isScriptStorageCapacityLegal(outputScriptDTO)){
            return false;
        }
        return ScriptTool.isPayToPublicKeyHashOutputScript(outputScriptDTO);
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
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                //跳过数据
                ++i;
            }else {
                return false;
            }
        }
        if(calculateScriptSize(scriptDTO) > GlobalSetting.ScriptConstant.SCRIPT_TEXT_MAX_SIZE){
            LogUtil.debug("交易校验失败：交易输出脚本所占存储空间超出限制。");
            return false;
        }
        return true;
    }
    //endregion



    //region 计算大小
    public static long calculateBlockSize(Block block) {
        return calculateBlockSize(Model2DtoTool.block2BlockDTO(block));
    }
    /**
     * 计算区块的大小
     */
    public static long calculateBlockSize(BlockDTO blockDTO) {
        long size = 0;
        long timestamp = blockDTO.getTimestamp();
        size += longSize(timestamp);

        String previousBlockHash = blockDTO.getPreviousHash();
        size += stringSize(previousBlockHash);

        String nonce = blockDTO.getNonce();
        size += stringSize(nonce);
        List<TransactionDTO> transactionDtoList = blockDTO.getTransactions();
        for(TransactionDTO transactionDTO:transactionDtoList){
            size += calculateTransactionSize(transactionDTO);
        }
        return size;
    }

    public static long calculateTransactionSize(Transaction transaction) {
        return calculateTransactionSize(Model2DtoTool.transaction2TransactionDTO(transaction));
    }
    public static long calculateTransactionSize(TransactionDTO transactionDTO) {
        long size = 0;
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getInputs();
        size += calculateTransactionInputSize(transactionInputDtoList);
        List<TransactionOutputDTO> transactionOutputDtoList = transactionDTO.getOutputs();
        size += calculateTransactionOutputSize(transactionOutputDtoList);
        return size;
    }
    private static long calculateTransactionOutputSize(List<TransactionOutputDTO> transactionOutputDtoList) {
        long size = 0;
        if(transactionOutputDtoList == null || transactionOutputDtoList.size()==0){
            return size;
        }
        for(TransactionOutputDTO transactionOutputDTO:transactionOutputDtoList){
            size += calculateTransactionOutputSize(transactionOutputDTO);
        }
        return size;
    }
    private static long calculateTransactionOutputSize(TransactionOutputDTO transactionOutputDTO) {
        long size = 0;
        OutputScriptDTO outputScriptDTO = transactionOutputDTO.getOutputScript();
        size += calculateScriptSize(outputScriptDTO);
        long value = transactionOutputDTO.getValue();
        size += longSize(value);
        return size;
    }
    private static long calculateTransactionInputSize(List<TransactionInputDTO> inputs) {
        long size = 0;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInputDTO transactionInputDTO:inputs){
            size += calculateTransactionInputSize(transactionInputDTO);
        }
        return size;
    }
    private static long calculateTransactionInputSize(TransactionInputDTO input) {
        long size = 0;
        String transactionHash = input.getTransactionHash();
        size += stringSize(transactionHash);
        long transactionOutputIndex = input.getTransactionOutputIndex();
        size += longSize(transactionOutputIndex);
        InputScriptDTO inputScriptDTO = input.getInputScript();
        size += calculateScriptSize(inputScriptDTO);
        return size;
    }
    private static long calculateScriptSize(ScriptDTO script) {
        long size = 0;
        if(script == null || script.size()==0){
            return size;
        }
        for(String scriptCode:script){
            size += scriptCode.length();
        }
        return size;
    }


    private static long stringSize(String string) {
        return string.length();
    }

    private static long longSize(long timestamp) {
        return String.valueOf(timestamp).length();
    }
    //endregion
}
