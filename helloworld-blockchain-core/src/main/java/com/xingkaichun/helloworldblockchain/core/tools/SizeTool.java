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
     * 校验区块大小。用来限制区块的大小。
     * 注意：校验区块的大小，不仅要校验区块的大小
     * ，还要校验区块内部各个属性(时间戳、前哈希、随机数、交易)的大小。
     */
    public static boolean checkBlockSize(Block block) {
        return checkBlockSize(Model2DtoTool.block2BlockDto(block));
    }
    public static boolean checkBlockSize(BlockDto blockDto) {
        //区块的时间戳的长度不需要校验  假设时间戳长度不正确，则在随后的业务逻辑中走不通

        //区块的前哈希的长度不需要校验  假设前哈希长度不正确，则在随后的业务逻辑中走不通

        //校验区块随机数大小
        long nonceSize = stringSize(blockDto.getNonce());
        if(!LongUtil.isEquals(nonceSize, Setting.BlockSetting.NONCE_SIZE)){
            LogUtil.debug(StringUtil.format("nonce[%s]长度不是[%s]。",blockDto.getNonce(), Setting.BlockSetting.NONCE_SIZE));
            return false;
        }

        //校验每一笔交易大小
        List<TransactionDto> transactionDtoList = blockDto.getTransactions();
        if(transactionDtoList != null){
            for(TransactionDto transactionDto:transactionDtoList){
                if(!checkTransactionSize(transactionDto)){
                    LogUtil.debug("交易数据异常，交易大小非法。");
                    return false;
                }
            }
        }

        //校验区块占用的存储空间
        long blockSize = calculateBlockSize(blockDto);
        if(blockSize > Setting.BlockSetting.BLOCK_MAX_SIZE){
            LogUtil.debug(String.format("区块数据大小[%s]超过限制[%s]。",blockSize, Setting.BlockSetting.BLOCK_MAX_SIZE));
            return false;
        }
        return true;
    }
    /**
     * 校验交易的大小：用来限制交易的大小。
     * 注意：校验交易的大小，不仅要校验交易的大小
     * ，还要校验交易内部各个属性(交易输入、交易输出)的大小。
     */
    public static boolean checkTransactionSize(Transaction transaction) {
        return checkTransactionSize(Model2DtoTool.transaction2TransactionDto(transaction));
    }
    public static boolean checkTransactionSize(TransactionDto transactionDto) {
        //校验交易输入
        List<TransactionInputDto> transactionInputDtoList = transactionDto.getInputs();
        if(transactionInputDtoList != null){
            for(TransactionInputDto transactionInputDto:transactionInputDtoList){
                //交易的未花费输出大小不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本大小
                InputScriptDto inputScriptDto = transactionInputDto.getInputScript();
                //校验输入脚本的大小
                if(!checkInputScriptSize(inputScriptDto)){
                    return false;
                }
            }
        }

        //校验交易输出
        List<TransactionOutputDto> transactionOutputDtoList = transactionDto.getOutputs();
        if(transactionOutputDtoList != null){
            for(TransactionOutputDto transactionOutputDto:transactionOutputDtoList){
                //交易输出金额大小不需要校验  假设不正确，则在随后的业务逻辑中走不通

                //校验脚本大小
                OutputScriptDto outputScriptDto = transactionOutputDto.getOutputScript();
                //校验输出脚本的大小
                if(!checkOutputScriptSize(outputScriptDto)){
                    return false;
                }

            }
        }

        //校验整笔交易大小十分合法
        long transactionSize = calculateTransactionSize(transactionDto);
        if(transactionSize > Setting.TransactionSetting.TRANSACTION_MAX_SIZE){
            LogUtil.debug(StringUtil.format("交易[%s]字符超过大小限制值[%s]。",transactionSize, Setting.TransactionSetting.TRANSACTION_MAX_SIZE));
            return false;
        }
        return true;
    }

    /**
     * 校验输入脚本的大小
     */
    private static boolean checkInputScriptSize(InputScriptDto inputScriptDto) {
        //先宽泛的校验脚本大小
        if(!checkScriptSize(inputScriptDto)){
            return false;
        }
        //严格校验输入脚本大小，因为当前输入脚本只有P2PKH，所以只需校验输入脚本是否是P2PKH输入脚本即可。
        return ScriptTool.isPayToPublicKeyHashInputScript(inputScriptDto);
    }

    /**
     * 校验输出脚本的大小
     */
    public static boolean checkOutputScriptSize(OutputScriptDto outputScriptDto) {
        //先宽泛的校验脚本大小
        if(!checkScriptSize(outputScriptDto)){
            return false;
        }
        //严格校验输入脚本大小，因为当前输出脚本只有P2PKH，所以只需校验输出脚本是否是P2PKH输出脚本即可。
        return ScriptTool.isPayToPublicKeyHashOutputScript(outputScriptDto);
    }

    /**
     * 校验脚本的大小
     */
    public static boolean checkScriptSize(ScriptDto scriptDto) {
        for(int i=0;i<scriptDto.size();i++){
            String operationCode = scriptDto.get(i);
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
        if(calculateScriptSize(scriptDto) > Setting.ScriptSetting.SCRIPT_MAX_SIZE){
            LogUtil.debug("交易校验失败：交易输出脚本大小超出限制。");
            return false;
        }
        return true;
    }
    //endregion



    //region 计算大小
    public static long calculateBlockSize(Block block) {
        return calculateBlockSize(Model2DtoTool.block2BlockDto(block));
    }
    public static long calculateBlockSize(BlockDto blockDto) {
        long size = 0;
        long timestamp = blockDto.getTimestamp();
        size += longSize(timestamp);

        String previousBlockHash = blockDto.getPreviousHash();
        size += stringSize(previousBlockHash);

        String nonce = blockDto.getNonce();
        size += stringSize(nonce);
        List<TransactionDto> transactionDtoList = blockDto.getTransactions();
        for(TransactionDto transactionDto:transactionDtoList){
            size += calculateTransactionSize(transactionDto);
        }
        return size;
    }
    public static long calculateTransactionSize(Transaction transaction) {
        return calculateTransactionSize(Model2DtoTool.transaction2TransactionDto(transaction));
    }
    public static long calculateTransactionSize(TransactionDto transactionDto) {
        long size = 0;
        List<TransactionInputDto> transactionInputDtoList = transactionDto.getInputs();
        size += calculateTransactionInputSize(transactionInputDtoList);
        List<TransactionOutputDto> transactionOutputDtoList = transactionDto.getOutputs();
        size += calculateTransactionOutputSize(transactionOutputDtoList);
        return size;
    }
    private static long calculateTransactionOutputSize(List<TransactionOutputDto> transactionOutputDtoList) {
        long size = 0;
        if(transactionOutputDtoList == null || transactionOutputDtoList.size()==0){
            return size;
        }
        for(TransactionOutputDto transactionOutputDto:transactionOutputDtoList){
            size += calculateTransactionOutputSize(transactionOutputDto);
        }
        return size;
    }
    private static long calculateTransactionOutputSize(TransactionOutputDto transactionOutputDto) {
        long size = 0;
        OutputScriptDto outputScriptDto = transactionOutputDto.getOutputScript();
        size += calculateScriptSize(outputScriptDto);
        long value = transactionOutputDto.getValue();
        size += longSize(value);
        return size;
    }
    private static long calculateTransactionInputSize(List<TransactionInputDto> inputs) {
        long size = 0;
        if(inputs == null || inputs.size()==0){
            return size;
        }
        for(TransactionInputDto transactionInputDto:inputs){
            size += calculateTransactionInputSize(transactionInputDto);
        }
        return size;
    }
    private static long calculateTransactionInputSize(TransactionInputDto input) {
        long size = 0;
        String transactionHash = input.getTransactionHash();
        size += stringSize(transactionHash);
        long transactionOutputIndex = input.getTransactionOutputIndex();
        size += longSize(transactionOutputIndex);
        InputScriptDto inputScriptDto = input.getInputScript();
        size += calculateScriptSize(inputScriptDto);
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
