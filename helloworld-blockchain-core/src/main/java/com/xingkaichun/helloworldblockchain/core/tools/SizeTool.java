package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.setting.Setting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.LongUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * (区块、交易)大小工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class SizeTool {

    //region 校验大小
    /**
     * 校验区块大小是否合法。用来限制区块的大小。
     * 注意：校验区块的大小是否合法，不仅要校验区块的大小
     * ，还要校验区块内部各个属性(时间戳、前哈希、随机数、交易)的大小是否合法。
     */
    public static boolean isBlockSizeLegal(Block block) {
        return isBlockSizeLegal(Model2DtoTool.block2BlockDTO(block));
    }
    public static boolean isBlockSizeLegal(BlockDto blockDTO) {
        //区块的时间戳的长度不需要校验  假设时间戳长度不正确，则在随后的业务逻辑中走不通

        //区块的前哈希的长度不需要校验  假设前哈希长度不正确，则在随后的业务逻辑中走不通

        //校验区块随机数大小
        long nonceSize = stringSize(blockDTO.getNonce());
        if(!LongUtil.isEquals(nonceSize, Setting.BlockSetting.NONCE_SIZE)){
            LogUtil.debug(StringUtil.format("nonce[%s]长度不是[%s]。",blockDTO.getNonce(), Setting.BlockSetting.NONCE_SIZE));
            return false;
        }

        //校验每一笔交易大小是否合法
        List<TransactionDto> transactionDtoList = blockDTO.getTransactions();
        if(transactionDtoList != null){
            for(TransactionDto transactionDTO:transactionDtoList){
                if(!isTransactionSizeLegal(transactionDTO)){
                    LogUtil.debug("交易数据异常，交易大小非法。");
                    return false;
                }
            }
        }

        //校验区块占用的存储空间
        long blockSize = calculateBlockSize(blockDTO);
        if(blockSize > Setting.BlockSetting.BLOCK_MAX_SIZE){
            LogUtil.debug(String.format("区块数据大小[%s]超过限制[%s]。",blockSize, Setting.BlockSetting.BLOCK_MAX_SIZE));
            return false;
        }
        return true;
    }
    /**
     * 校验交易的大小是否合法：用来限制交易的大小。
     * 注意：校验交易的大小是否合法，不仅要校验交易的大小
     * ，还要校验交易内部各个属性(交易输入、交易输出)的大小是否合法。
     */
    public static boolean isTransactionSizeLegal(Transaction transaction) {
        return isTransactionSizeLegal(Model2DtoTool.transaction2TransactionDTO(transaction));
    }
    public static boolean isTransactionSizeLegal(TransactionDto transactionDTO) {
        //校验交易输入
        List<TransactionInputDto> transactionInputDtoList = transactionDTO.getInputs();
        if(transactionInputDtoList != null){
            for(TransactionInputDto transactionInputDTO:transactionInputDtoList){
                //交易的未花费输出大小不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本大小
                InputScriptDto inputScriptDTO = transactionInputDTO.getInputScript();
                //校验输入脚本的大小
                if(!isInputScriptSizeLegal(inputScriptDTO)){
                    return false;
                }
            }
        }

        //校验交易输出
        List<TransactionOutputDto> transactionOutputDtoList = transactionDTO.getOutputs();
        if(transactionOutputDtoList != null){
            for(TransactionOutputDto transactionOutputDTO:transactionOutputDtoList){
                //交易输出金额大小不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本大小
                OutputScriptDto outputScriptDTO = transactionOutputDTO.getOutputScript();
                //校验输出脚本的大小
                if(!isOutputScriptSizeLegal(outputScriptDTO)){
                    return false;
                }

            }
        }

        //校验整笔交易大小十分合法
        long transactionSize = calculateTransactionSize(transactionDTO);
        if(transactionSize > Setting.TransactionSetting.TRANSACTION_MAX_SIZE){
            LogUtil.debug(StringUtil.format("交易[%s]字符超过大小限制值[%s]。",transactionSize, Setting.TransactionSetting.TRANSACTION_MAX_SIZE));
            return false;
        }
        return true;
    }

    /**
     * 校验交易输入的大小
     */
    private static boolean isInputScriptSizeLegal(InputScriptDto inputScriptDTO) {
        //先宽泛的校验脚本大小
        if(!isScriptSizeLegal(inputScriptDTO)){
            return false;
        }
        //严格校验输入脚本大小，因为当前输入脚本只有P2PKH，所以只需校验输入脚本是否是P2PKH输入脚本即可。
        return ScriptTool.isPayToPublicKeyHashInputScript(inputScriptDTO);
    }

    /**
     * 校验交易输出的大小
     */
    public static boolean isOutputScriptSizeLegal(OutputScriptDto outputScriptDTO) {
        //先宽泛的校验脚本大小
        if(!isScriptSizeLegal(outputScriptDTO)){
            return false;
        }
        //严格校验输入脚本大小，因为当前输出脚本只有P2PKH，所以只需校验输出脚本是否是P2PKH输出脚本即可。
        return ScriptTool.isPayToPublicKeyHashOutputScript(outputScriptDTO);
    }

    /**
     * 校验脚本的大小
     */
    public static boolean isScriptSizeLegal(ScriptDto scriptDTO) {
        for(int i=0;i<scriptDTO.size();i++){
            String operationCode = scriptDTO.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                continue;
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                //跳过操作数
                ++i;
            }else {
                return false;
            }
        }
        if(calculateScriptSize(scriptDTO) > Setting.ScriptSetting.SCRIPT_MAX_SIZE){
            LogUtil.debug("交易校验失败：交易输出脚本大小超出限制。");
            return false;
        }
        return true;
    }
    //endregion



    //region 计算大小
    public static long calculateBlockSize(Block block) {
        return calculateBlockSize(Model2DtoTool.block2BlockDTO(block));
    }
    public static long calculateBlockSize(BlockDto blockDTO) {
        long size = 0;
        long timestamp = blockDTO.getTimestamp();
        size += longSize(timestamp);

        String previousBlockHash = blockDTO.getPreviousHash();
        size += stringSize(previousBlockHash);

        String nonce = blockDTO.getNonce();
        size += stringSize(nonce);
        List<TransactionDto> transactionDtoList = blockDTO.getTransactions();
        for(TransactionDto transactionDTO:transactionDtoList){
            size += calculateTransactionSize(transactionDTO);
        }
        return size;
    }
    public static long calculateTransactionSize(Transaction transaction) {
        return calculateTransactionSize(Model2DtoTool.transaction2TransactionDTO(transaction));
    }
    public static long calculateTransactionSize(TransactionDto transactionDTO) {
        long size = 0;
        List<TransactionInputDto> transactionInputDtoList = transactionDTO.getInputs();
        size += calculateTransactionInputSize(transactionInputDtoList);
        List<TransactionOutputDto> transactionOutputDtoList = transactionDTO.getOutputs();
        size += calculateTransactionOutputSize(transactionOutputDtoList);
        return size;
    }
    private static long calculateTransactionOutputSize(List<TransactionOutputDto> transactionOutputDtoList) {
        long size = 0;
        if(transactionOutputDtoList == null || transactionOutputDtoList.size()==0){
            return size;
        }
        for(TransactionOutputDto transactionOutputDTO:transactionOutputDtoList){
            size += calculateTransactionOutputSize(transactionOutputDTO);
        }
        return size;
    }
    private static long calculateTransactionOutputSize(TransactionOutputDto transactionOutputDTO) {
        long size = 0;
        OutputScriptDto outputScriptDTO = transactionOutputDTO.getOutputScript();
        size += calculateScriptSize(outputScriptDTO);
        long value = transactionOutputDTO.getValue();
        size += longSize(value);
        return size;
    }
    private static long calculateTransactionInputSize(List<TransactionInputDto> inputs) {
        long size = 0;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInputDto transactionInputDTO:inputs){
            size += calculateTransactionInputSize(transactionInputDTO);
        }
        return size;
    }
    private static long calculateTransactionInputSize(TransactionInputDto input) {
        long size = 0;
        String transactionHash = input.getTransactionHash();
        size += stringSize(transactionHash);
        long transactionOutputIndex = input.getTransactionOutputIndex();
        size += longSize(transactionOutputIndex);
        InputScriptDto inputScriptDTO = input.getInputScript();
        size += calculateScriptSize(inputScriptDTO);
        return size;
    }
    private static long calculateScriptSize(ScriptDto script) {
        long size = 0;
        if(script == null || script.size()==0){
            return size;
        }
        for(String scriptCode:script){
            size += stringSize(scriptCode);
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
