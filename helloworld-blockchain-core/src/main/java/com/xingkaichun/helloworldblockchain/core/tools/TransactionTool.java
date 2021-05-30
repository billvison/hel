package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.VirtualMachine;
import com.xingkaichun.helloworldblockchain.core.impl.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.script.BooleanEnum;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

import java.util.*;

/**
 * Transaction工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionTool {

    /**
     * 交易输入总额
     */
    public static long getInputsValue(Transaction transaction) {
        return getInputsValue(transaction.getInputs());
    }
    /**
     * 交易输入总额
     */
    public static long getInputsValue(List<TransactionInput> inputs) {
        long total = 0;
        if(inputs != null){
            for(TransactionInput input : inputs) {
                total += input.getUnspentTransactionOutput().getValue();
            }
        }
        return total;
    }



    /**
     * 交易输出总额
     */
    public static long getOutputsValue(Transaction transaction) {
        return getOutputsValue(transaction.getOutputs());
    }
    /**
     * 交易输出总额
     */
    public static long getOutputsValue(List<TransactionOutput> outputs) {
        long total = 0;
        if(outputs != null){
            for(TransactionOutput o : outputs) {
                total += o.getValue();
            }
        }
        return total;
    }


    /**
     * 交易手续费（只计算标准交易的手续费，创世交易抛出异常）
     */
    public static long getTransactionFee(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.STANDARD){
            long fee = getInputsValue(transaction) - getOutputsValue(transaction);
            return fee;
        }else {
            throw new RuntimeException("只能计算标准交易类型的手续费");
        }
    }
    /**
     * 交易费率（只计算标准交易的手续费，创世交易抛出异常）
     */
    public static long getFeeRate(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.STANDARD){
            return TransactionTool.getTransactionFee(transaction)/SizeTool.calculateTransactionSize(transaction);
        }else {
            throw new RuntimeException("只能计算标准交易类型的手续费");
        }
    }


    /**
     * 获取待签名数据
     */
    public static String signatureHashAll(Transaction transaction) {
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return signatureHashAll(transactionDto);
    }
    public static String signatureHashAll(TransactionDto transactionDto) {
        byte[] bytesTransaction = bytesTransaction(transactionDto,true);
        byte[] sha256Digest = SHA256Util.doubleDigest(bytesTransaction);
        return HexUtil.bytesToHexString(sha256Digest);
    }

    /**
     * 交易签名
     */
    public static String signature(String privateKey, Transaction transaction) {
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return signature(privateKey,transactionDto);
    }
    public static String signature(String privateKey, TransactionDto transactionDto) {
        String signatureHashAll = signatureHashAll(transactionDto);
        String signature = AccountUtil.signature(privateKey,signatureHashAll);
        return signature;
    }




    /**
     * 验证脚本
     */
    public static boolean verifyScript(Transaction transaction) {
        try{
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                for(TransactionInput transactionInput:inputs){
                    Script script = ScriptTool.createScript(transactionInput.getInputScript(),transactionInput.getUnspentTransactionOutput().getOutputScript());
                    VirtualMachine virtualMachine = new StackBasedVirtualMachine();
                    ScriptExecuteResult scriptExecuteResult = virtualMachine.executeScript(transaction,script);
                    boolean executeSuccess = scriptExecuteResult.size()==1 && Arrays.equals(BooleanEnum.TRUE.getCode(),HexUtil.hexStringToBytes(scriptExecuteResult.pop()));
                    if(!executeSuccess){
                        return false;
                    }
                }
            }
        }catch (Exception e){
            LogUtil.error("交易校验失败：交易[输入脚本]解锁交易[输出脚本]异常。",e);
            return false;
        }
        return true;
    }




    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(Transaction transaction){
        return calculateTransactionHash(Model2DtoTool.transaction2TransactionDto(transaction));
    }
    public static String calculateTransactionHash(TransactionDto transactionDto){
        byte[] bytesTransaction = bytesTransaction(transactionDto,false);
        byte[] sha256Digest = SHA256Util.doubleDigest(bytesTransaction);
        return HexUtil.bytesToHexString(sha256Digest);
    }




    //region 序列化与反序列化
    /**
     * 序列化。将交易转换为字节数组，要求生成的字节数组反过来能还原为原始交易。
     */
    public static byte[] bytesTransaction(TransactionDto transactionDto, boolean omitInputScript) {
        List<byte[]> bytesUnspentTransactionOutputs = new ArrayList<>();
        List<TransactionInputDto> inputs = transactionDto.getInputs();
        if(inputs != null){
            for(TransactionInputDto transactionInputDto:inputs){
                byte[] bytesTransactionHash = HexUtil.hexStringToBytes(transactionInputDto.getTransactionHash());
                byte[] bytesTransactionOutputIndex = ByteUtil.long8ToByte8(transactionInputDto.getTransactionOutputIndex());

                byte[] bytesUnspentTransactionOutput = ByteUtil.concat(ByteUtil.concatLength(bytesTransactionHash),
                        ByteUtil.concatLength(bytesTransactionOutputIndex));
                if(!omitInputScript){
                    byte[] bytesInputScript = ScriptTool.bytesScript(transactionInputDto.getInputScript());
                    bytesUnspentTransactionOutput = ByteUtil.concat(bytesUnspentTransactionOutput,ByteUtil.concatLength(bytesInputScript));
                }
                bytesUnspentTransactionOutputs.add(ByteUtil.concatLength(bytesUnspentTransactionOutput));
            }
        }

        List<byte[]> bytesTransactionOutputs = new ArrayList<>();
        List<TransactionOutputDto> outputs = transactionDto.getOutputs();
        if(outputs != null){
            for(TransactionOutputDto transactionOutputDto:outputs){
                byte[] bytesOutputScript = ScriptTool.bytesScript(transactionOutputDto.getOutputScript());
                byte[] bytesValue = ByteUtil.long8ToByte8(transactionOutputDto.getValue());
                byte[] bytesTransactionOutput = ByteUtil.concat(ByteUtil.concatLength(bytesOutputScript),ByteUtil.concatLength(bytesValue));
                bytesTransactionOutputs.add(ByteUtil.concatLength(bytesTransactionOutput));
            }
        }

        byte[] data = ByteUtil.concat(ByteUtil.flatAndConcatLength(bytesUnspentTransactionOutputs),
                ByteUtil.flatAndConcatLength(bytesTransactionOutputs));
        return data;
    }
    /**
     * 反序列化。将字节数组转换为交易。
     */
    public static TransactionDto transactionDto(byte[] bytesTransaction, boolean omitInputScript) {
        TransactionDto transactionDto = new TransactionDto();
        int start = 0;
        long bytesTransactionInputDtosLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransaction,start,start+8));
        start += 8;
        byte[] bytesTransactionInputDtos = Arrays.copyOfRange(bytesTransaction,start, start+(int) bytesTransactionInputDtosLength);
        start += bytesTransactionInputDtosLength;
        List<TransactionInputDto> transactionInputDtos = transactionInputDtos(bytesTransactionInputDtos,omitInputScript);
        transactionDto.setInputs(transactionInputDtos);

        long bytesTransactionOutputsLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransaction,start,start+8));
        start += 8;
        byte[] bytesTransactionOutputs = Arrays.copyOfRange(bytesTransaction,start, start+(int) bytesTransactionOutputsLength);
        start += bytesTransactionOutputsLength;
        List<TransactionOutputDto> transactionOutputDtos = transactionOutputDtos(bytesTransactionOutputs);
        transactionDto.setOutputs(transactionOutputDtos);
        return transactionDto;
    }
    private static List<TransactionOutputDto> transactionOutputDtos(byte[] bytesTransactionOutputs) {
        if(bytesTransactionOutputs == null || bytesTransactionOutputs.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionOutputDto> transactionOutputDtos = new ArrayList<>();
        while (start < bytesTransactionOutputs.length){
            long bytesTransactionOutputDtoLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransactionOutputs,start,start+8));
            start += 8;
            byte[] bytesTransactionOutput = Arrays.copyOfRange(bytesTransactionOutputs,start, start+(int) bytesTransactionOutputDtoLength);
            start += bytesTransactionOutputDtoLength;
            TransactionOutputDto transactionOutputDto = transactionOutputDto(bytesTransactionOutput);
            transactionOutputDtos.add(transactionOutputDto);
            if(start >= bytesTransactionOutputs.length){
                break;
            }
        }
        return transactionOutputDtos;
    }
    private static TransactionOutputDto transactionOutputDto(byte[] bytesTransactionOutput) {
        int start = 0;
        long bytesOutputScriptLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransactionOutput,start,start+8));
        start += 8;
        byte[] bytesOutputScript = Arrays.copyOfRange(bytesTransactionOutput,start, start+(int) bytesOutputScriptLength);
        start += bytesOutputScriptLength;
        OutputScriptDto outputScriptDto = ScriptTool.outputScriptDto(bytesOutputScript);

        long bytesValueLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransactionOutput,start,start+8));
        start += 8;
        byte[] bytesValue = Arrays.copyOfRange(bytesTransactionOutput,start, start+(int) bytesValueLength);
        start += bytesValueLength;

        TransactionOutputDto transactionOutputDto = new TransactionOutputDto();
        transactionOutputDto.setOutputScript(outputScriptDto);
        transactionOutputDto.setValue(ByteUtil.byte8ToLong8(bytesValue));
        return transactionOutputDto;
    }
    private static List<TransactionInputDto> transactionInputDtos(byte[] bytesTransactionInputDtos, boolean omitInputScript) {
        if(bytesTransactionInputDtos == null || bytesTransactionInputDtos.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionInputDto> transactionInputDtos = new ArrayList<>();
        while (start < bytesTransactionInputDtos.length){
            long bytesTransactionInputDtoLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransactionInputDtos,start,start+8));
            start += 8;
            byte[] bytesTransactionInput = Arrays.copyOfRange(bytesTransactionInputDtos,start, start+(int) bytesTransactionInputDtoLength);
            start += bytesTransactionInputDtoLength;
            TransactionInputDto transactionInputDto = transactionInputDto(bytesTransactionInput,omitInputScript);
            transactionInputDtos.add(transactionInputDto);
            if(start >= bytesTransactionInputDtos.length){
                break;
            }
        }
        return transactionInputDtos;
    }
    private static TransactionInputDto transactionInputDto(byte[] bytesTransactionInputDto, boolean omitInputScript) {
        int start = 0;
        long bytesTransactionHashLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransactionInputDto,start,start+8));
        start += 8;
        byte[] bytesTransactionHash = Arrays.copyOfRange(bytesTransactionInputDto,start, start+(int) bytesTransactionHashLength);
        start += bytesTransactionHashLength;

        long bytesTransactionOutputIndexLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransactionInputDto,start,start+8));
        start += 8;
        byte[] bytesTransactionOutputIndex = Arrays.copyOfRange(bytesTransactionInputDto,start, start+(int) bytesTransactionOutputIndexLength);
        start += bytesTransactionOutputIndexLength;

        TransactionInputDto transactionInputDto = new TransactionInputDto();
        if(!omitInputScript){
            long bytesOutputScriptLength = ByteUtil.byte8ToLong8(Arrays.copyOfRange(bytesTransactionInputDto,start,start+8));
            start += 8;
            byte[] bytesOutputScript = Arrays.copyOfRange(bytesTransactionInputDto,start, start+(int) bytesOutputScriptLength);
            start += bytesOutputScriptLength;
            InputScriptDto inputScriptDto = ScriptTool.inputScriptDto(bytesOutputScript);
            transactionInputDto.setInputScript(inputScriptDto);
        }
        transactionInputDto.setTransactionHash(HexUtil.bytesToHexString(bytesTransactionHash));
        transactionInputDto.setTransactionOutputIndex(ByteUtil.byte8ToLong8(bytesTransactionOutputIndex));
        return transactionInputDto;
    }
    //endregion

    /**
     * 交易中的金额是否符合系统的约束
     */
    public static boolean isTransactionAmountLegal(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            //校验交易输入的金额
            for(TransactionInput input:inputs){
                if(!isTransactionAmountLegal(input.getUnspentTransactionOutput().getValue())){
                    LogUtil.debug("交易金额不合法");
                    return false;
                }
            }
        }

        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            //校验交易输出的金额
            for(TransactionOutput output:outputs){
                if(!isTransactionAmountLegal(output.getValue())){
                    LogUtil.debug("交易金额不合法");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 交易中的地址是否符合系统的约束
     */
    public static boolean isTransactionAddressIllegal(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                if(!AccountUtil.isPayToPublicKeyHashAddress(output.getAddress())){
                    LogUtil.debug("交易地址不合法");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是一个合法的交易金额：这里用于限制交易金额的最大值、最小值、小数保留位置
     */
    public static boolean isTransactionAmountLegal(long transactionAmount) {
        try {
            //交易金额不能小于等于0
            if(transactionAmount <= 0){
                LogUtil.debug("交易金额不合法：交易金额不能小于等于0");
                return false;
            }
            //交易金额最小值不需要校验，假设值不正确，业务逻辑通过不了。

            //交易金额最大值不需要校验，假设值不正确，业务逻辑通过不了
            return true;
        } catch (Exception e) {
            LogUtil.error("校验金额方法出现异常，请检查。",e);
            return false;
        }
    }

    /**
     * 是否存在重复的交易输入
     */
    public static boolean isExistDuplicateTransactionInput(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs == null || inputs.size()==0){
            return false;
        }
        Set<String> transactionOutputIdSet = new HashSet<>();
        for(TransactionInput transactionInput : inputs) {
            TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
            String transactionOutputId = unspentTransactionOutput.getTransactionOutputId();
            if(transactionOutputIdSet.contains(transactionOutputId)){
                return true;
            }
            transactionOutputIdSet.add(transactionOutputId);
        }
        return false;
    }
    /**
     * 区块新产生的地址是否存在重复
     */
    public static boolean isExistDuplicateNewAddress(Transaction transaction) {
        Set<String> addressSet = new HashSet<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs){
                String address = output.getAddress();
                if(addressSet.contains(address)){
                    LogUtil.debug(String.format("区块数据异常，地址[%s]重复。",address));
                    return true;
                }else {
                    addressSet.add(address);
                }
            }
        }
        return false;
    }

    public static UnspentTransactionOutput transactionOutput2UnspentTransactionOutput(TransactionOutput transactionOutput) {
        //UnspentTransactionOutput是TransactionOutput子类，且没有其它的属性才可以这样转换。
        String json = JsonUtil.toJson(transactionOutput);
        UnspentTransactionOutput unspentTransactionOutput = JsonUtil.fromJson(json,UnspentTransactionOutput.class);
        return unspentTransactionOutput;
    }

    public static long getTransactionInputCount(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        long transactionInputCount = inputs==null?0:inputs.size();
        return transactionInputCount;
    }

    public static long getTransactionOutputCount(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        long transactionOutputCount = outputs==null?0:outputs.size();
        return transactionOutputCount;
    }

    public static long calculateTransactionFee(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.GENESIS){
            //创世交易没有交易手续费
            return 0;
        }else if(transaction.getTransactionType() == TransactionType.STANDARD){
            long inputsValue = getInputsValue(transaction);
            long outputsValue = getOutputsValue(transaction);
            return inputsValue - outputsValue;
        }else {
            throw new RuntimeException("没有该交易类型。");
        }
    }

    /**
     * 按照费率(每字符的手续费)从大到小排序交易
     */
    public static void sortByFeeRateDescend(List<Transaction> transactions) {
        if(transactions == null){
            return;
        }
        transactions.sort((transaction1, transaction2) -> {
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
    }
}
