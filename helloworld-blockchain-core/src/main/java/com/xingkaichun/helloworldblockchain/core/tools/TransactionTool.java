package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Transaction工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionTool {

    private static final Logger logger = LoggerFactory.getLogger(TransactionTool.class);

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
                total += input.getUnspendTransactionOutput().getValue();
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
     * 获取待签名数据
     */
    public static byte[] hash4SignatureHashAll(Transaction transaction) {
        TransactionDTO transactionDTO = Model2DtoTool.transaction2TransactionDTO(transaction);
        return hash4SignatureHashAll(transactionDTO);
    }
    public static byte[] hash4SignatureHashAll(TransactionDTO transactionDTO) {
        byte[] bytesTransaction = bytesTransactio4SignatureHashAll(transactionDTO);
        byte[] sha256Digest = SHA256Util.doubleDigest(bytesTransaction);
        return sha256Digest;
    }

    /**
     * 待签名的数据 方法来源于本类中的bytesTransaction方法，移除了脚本输入。请保证在移除脚本输入后，该方法可以反序列化。
     */
    public static byte[] bytesTransactio4SignatureHashAll(TransactionDTO transactionDTO) {
        List<byte[]> bytesUnspendTransactionOutputList = new ArrayList<>();
        List<TransactionInputDTO> inputs = transactionDTO.getInputs();
        if(inputs != null){
            for(TransactionInputDTO transactionInputDTO:inputs){
                byte[] bytesTransactionHash = HexUtil.hexStringToBytes(transactionInputDTO.getTransactionHash());
                byte[] bytesTransactionOutputIndex = ByteUtil.longToBytes8BigEndian(transactionInputDTO.getTransactionOutputIndex());

                byte[] bytesUnspendTransactionOutput = Bytes.concat(ByteUtil.concatLengthBytes(bytesTransactionHash),
                        ByteUtil.concatLengthBytes(bytesTransactionOutputIndex));
                bytesUnspendTransactionOutputList.add(ByteUtil.concatLengthBytes(bytesUnspendTransactionOutput));
            }
        }

        List<byte[]> bytesTransactionOutputList = new ArrayList<>();
        List<TransactionOutputDTO> outputs = transactionDTO.getOutputs();
        if(outputs != null){
            for(TransactionOutputDTO transactionOutputDTO:outputs){
                byte[] bytesOutputScript = ScriptTool.bytesScript(transactionOutputDTO.getOutputScript());
                byte[] bytesValue = ByteUtil.longToBytes8BigEndian(transactionOutputDTO.getValue());
                byte[] bytesTransactionOutput = Bytes.concat(ByteUtil.concatLengthBytes(bytesOutputScript),ByteUtil.concatLengthBytes(bytesValue));
                bytesTransactionOutputList.add(ByteUtil.concatLengthBytes(bytesTransactionOutput));
            }
        }

        byte[] data = Bytes.concat(ByteUtil.concatLengthBytes(bytesUnspendTransactionOutputList),
                ByteUtil.concatLengthBytes(bytesTransactionOutputList));

        return data;
    }


    /**
     * 交易签名
     */
    public static String signature(String privateKey, Transaction transaction) {
        TransactionDTO transactionDTO = Model2DtoTool.transaction2TransactionDTO(transaction);
        return signature(privateKey,transactionDTO);
    }
    public static String signature(String privateKey, TransactionDTO transactionDTO) {
        byte[] bytesMessage = hash4SignatureHashAll(transactionDTO);
        byte[] bytesSignature = AccountUtil.signature(privateKey,bytesMessage);
        String stringSignature = HexUtil.bytesToHexString(bytesSignature);
        return stringSignature;
    }




    /**
     * 验证脚本
     */
    public static boolean verifyScript(Transaction transaction) {
        try{
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                for(TransactionInput transactionInput:inputs){
                    Script payToClassicAddressScript = StackBasedVirtualMachine.createPayToClassicAddressScript(transactionInput.getInputScript(),transactionInput.getUnspendTransactionOutput().getOutputScript());
                    StackBasedVirtualMachine stackBasedVirtualMachine = new StackBasedVirtualMachine();
                    ScriptExecuteResult scriptExecuteResult = stackBasedVirtualMachine.executeScript(transaction,payToClassicAddressScript);
                    if(scriptExecuteResult.size()!=1 || !Boolean.valueOf(scriptExecuteResult.pop())){
                        return false;
                    }
                }
            }
        }catch (Exception e){
            logger.debug("交易校验失败：交易[输入脚本]解锁交易[输出脚本]异常。",e);
            return false;
        }
        return true;
    }




    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(Transaction transaction){
        return calculateTransactionHash(Model2DtoTool.transaction2TransactionDTO(transaction));
    }
    public static String calculateTransactionHash(TransactionDTO transactionDTO){
        byte[] bytesTransaction = bytesTransaction(transactionDTO);
        byte[] sha256Digest = SHA256Util.doubleDigest(bytesTransaction);
        return HexUtil.bytesToHexString(sha256Digest);
    }




    //region 序列化与反序列化
    /**
     * 序列化。将交易转换为字节数组，要求生成的字节数组反过来能还原为原始交易。
     */
    public static byte[] bytesTransaction(TransactionDTO transactionDTO) {
        List<byte[]> bytesUnspendTransactionOutputList = new ArrayList<>();
        List<TransactionInputDTO> inputs = transactionDTO.getInputs();
        if(inputs != null){
            for(TransactionInputDTO transactionInputDTO:inputs){
                byte[] bytesTransactionHash = HexUtil.hexStringToBytes(transactionInputDTO.getTransactionHash());
                byte[] bytesTransactionOutputIndex = ByteUtil.longToBytes8BigEndian(transactionInputDTO.getTransactionOutputIndex());
                byte[] bytesInputScript = ScriptTool.bytesScript(transactionInputDTO.getInputScript());

                byte[] bytesUnspendTransactionOutput = Bytes.concat(ByteUtil.concatLengthBytes(bytesTransactionHash),
                        ByteUtil.concatLengthBytes(bytesTransactionOutputIndex),
                        ByteUtil.concatLengthBytes(bytesInputScript));
                bytesUnspendTransactionOutputList.add(ByteUtil.concatLengthBytes(bytesUnspendTransactionOutput));
            }
        }

        List<byte[]> bytesTransactionOutputList = new ArrayList<>();
        List<TransactionOutputDTO> outputs = transactionDTO.getOutputs();
        if(outputs != null){
            for(TransactionOutputDTO transactionOutputDTO:outputs){
                byte[] bytesOutputScript = ScriptTool.bytesScript(transactionOutputDTO.getOutputScript());
                byte[] bytesValue = ByteUtil.longToBytes8BigEndian(transactionOutputDTO.getValue());
                byte[] bytesTransactionOutput = Bytes.concat(ByteUtil.concatLengthBytes(bytesOutputScript),ByteUtil.concatLengthBytes(bytesValue));
                bytesTransactionOutputList.add(ByteUtil.concatLengthBytes(bytesTransactionOutput));
            }
        }

        byte[] data = Bytes.concat(ByteUtil.concatLengthBytes(bytesUnspendTransactionOutputList),
                ByteUtil.concatLengthBytes(bytesTransactionOutputList));

        return data;
    }
    /**
     * 反序列化。将字节数组转换为交易
     */
    public static TransactionDTO transactionDTO(byte[] bytesTransaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        int start = 0;
        long bytesTransactionInputDtoListLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransaction,start,start+8));
        start += 8;
        byte[] bytesTransactionInputDtoList = Arrays.copyOfRange(bytesTransaction,start, start+(int) bytesTransactionInputDtoListLength);
        start += bytesTransactionInputDtoListLength;
        List<TransactionInputDTO> transactionInputDtoList = transactionInputDTOList(bytesTransactionInputDtoList);
        transactionDTO.setInputs(transactionInputDtoList);

        long bytesTransactionOutputListLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransaction,start,start+8));
        start += 8;
        byte[] bytesTransactionOutputList = Arrays.copyOfRange(bytesTransaction,start, start+(int) bytesTransactionOutputListLength);
        start += bytesTransactionOutputListLength;
        List<TransactionOutputDTO> transactionOutputDtoList = transactionOutputDTOList(bytesTransactionOutputList);
        transactionDTO.setOutputs(transactionOutputDtoList);
        return transactionDTO;
    }
    private static List<TransactionOutputDTO> transactionOutputDTOList(byte[] bytesTransactionOutputList) {
        if(bytesTransactionOutputList == null || bytesTransactionOutputList.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionOutputDTO> transactionOutputDTOList = new ArrayList<>();
        while (start < bytesTransactionOutputList.length){
            long bytesTransactionOutputDTOLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransactionOutputList,start,start+8));
            start += 8;
            byte[] bytesTransactionOutput = Arrays.copyOfRange(bytesTransactionOutputList,start, start+(int) bytesTransactionOutputDTOLength);
            start += bytesTransactionOutputDTOLength;
            TransactionOutputDTO transactionOutputDTO = transactionOutputDTO(bytesTransactionOutput);
            transactionOutputDTOList.add(transactionOutputDTO);
            if(start >= bytesTransactionOutputList.length){
                break;
            }
        }
        return transactionOutputDTOList;
    }
    private static TransactionOutputDTO transactionOutputDTO(byte[] bytesTransactionOutput) {
        int start = 0;
        long bytesOutputScriptLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransactionOutput,start,start+8));
        start += 8;
        byte[] bytesOutputScript = Arrays.copyOfRange(bytesTransactionOutput,start, start+(int) bytesOutputScriptLength);
        start += bytesOutputScriptLength;
        OutputScriptDTO outputScriptDTO = ScriptTool.outputScriptDTO(bytesOutputScript);

        long bytesValueLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransactionOutput,start,start+8));
        start += 8;
        byte[] bytesValue = Arrays.copyOfRange(bytesTransactionOutput,start, start+(int) bytesValueLength);
        start += bytesValueLength;

        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setOutputScript(outputScriptDTO);
        transactionOutputDTO.setValue(ByteUtil.bytes8BigEndianToLong(bytesValue));
        return transactionOutputDTO;
    }
    private static List<TransactionInputDTO> transactionInputDTOList(byte[] bytesTransactionInputDtoList) {
        if(bytesTransactionInputDtoList == null || bytesTransactionInputDtoList.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionInputDTO> transactionInputDTOList = new ArrayList<>();
        while (start < bytesTransactionInputDtoList.length){
            long bytesTransactionInputDTOLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransactionInputDtoList,start,start+8));
            start += 8;
            byte[] bytesTransactionInput = Arrays.copyOfRange(bytesTransactionInputDtoList,start, start+(int) bytesTransactionInputDTOLength);
            start += bytesTransactionInputDTOLength;
            TransactionInputDTO transactionInputDTO = transactionInputDTO(bytesTransactionInput);
            transactionInputDTOList.add(transactionInputDTO);
            if(start >= bytesTransactionInputDtoList.length){
                break;
            }
        }
        return transactionInputDTOList;
    }
    private static TransactionInputDTO transactionInputDTO(byte[] bytesTransactionInputDTO) {
        int start = 0;
        long bytesTransactionHashLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransactionInputDTO,start,start+8));
        start += 8;
        byte[] bytesTransactionHash = Arrays.copyOfRange(bytesTransactionInputDTO,start, start+(int) bytesTransactionHashLength);
        start += bytesTransactionHashLength;

        long bytesTransactionOutputIndexLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransactionInputDTO,start,start+8));
        start += 8;
        byte[] bytesTransactionOutputIndex = Arrays.copyOfRange(bytesTransactionInputDTO,start, start+(int) bytesTransactionOutputIndexLength);
        start += bytesTransactionOutputIndexLength;

        long bytesOutputScriptLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesTransactionInputDTO,start,start+8));
        start += 8;
        byte[] bytesOutputScript = Arrays.copyOfRange(bytesTransactionInputDTO,start, start+(int) bytesOutputScriptLength);
        start += bytesOutputScriptLength;
        InputScriptDTO inputScriptDTO = ScriptTool.inputScriptDTO(bytesOutputScript);


        TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
        transactionInputDTO.setInputScript(inputScriptDTO);
        UnspendTransactionOutputDTO unspendTransactionOutputDTO = new UnspendTransactionOutputDTO();
        unspendTransactionOutputDTO.setTransactionHash(HexUtil.bytesToHexString(bytesTransactionHash));
        unspendTransactionOutputDTO.setTransactionOutputIndex(ByteUtil.bytes8BigEndianToLong(bytesTransactionOutputIndex));
        transactionInputDTO.setTransactionHash(unspendTransactionOutputDTO.getTransactionHash());
        transactionInputDTO.setTransactionOutputIndex(unspendTransactionOutputDTO.getTransactionOutputIndex());
        return transactionInputDTO;
    }
    //endregion

    /**
     * 交易中的金额是否符合系统的约束
     */
    public static boolean isTransactionAmountLegal(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                if(!isTransactionAmountLegal(output.getValue())){
                    logger.debug("交易金额不合法");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否是一个合法的交易金额：这里用于限制交易金额的最大值、最小值、小数保留位置
     */
    public static boolean isTransactionAmountLegal(long transactionAmount) {
        try {
            //交易金额不能小于等于0
            if(transactionAmount <= 0){
                logger.debug("交易金额不合法：交易金额不能小于等于0");
                return false;
            }
            //交易金额最小值不需要校验，假设值不正确，业务逻辑通过不了。

            //交易金额最大值不需要校验，假设值不正确，业务逻辑通过不了
            return true;
        } catch (Exception e) {
            logger.debug("校验金额方法出现异常，请检查。",e);
            return false;
        }
    }

    /**
     * 校验激励
     */
    public static boolean isIncentiveRight(long targetMinerReward, Transaction transaction) {
        if(transaction.getTransactionType() != TransactionType.COINBASE){
            logger.debug("区块数据异常，区块中的第一笔交易应当是挖矿奖励交易。");
            return false;
        }
        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(inputs != null && inputs.size()!=0){
            logger.debug("区块数据异常，挖矿奖励交易交易输入应当是空。");
            return false;
        }
        if(outputs == null || outputs.size()!=1){
            logger.debug("区块数据异常，挖矿奖励交易只能有一个交易输出。");
            return false;
        }
        if(targetMinerReward < outputs.get(0).getValue()){
            logger.debug("挖矿奖励数据异常，挖矿奖励金额大于系统核算奖励金额。");
            return false;
        }
        return true;
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
            TransactionOutput unspendTransactionOutput = transactionInput.getUnspendTransactionOutput();
            String transactionOutputId = unspendTransactionOutput.getTransactionOutputId();
            if(transactionOutputIdSet.contains(transactionOutputId)){
                return true;
            }
            transactionOutputIdSet.add(transactionOutputId);
        }
        return false;
    }

    /**
     * 交易输入必须要大于交易输出
     */
    public static boolean isTransactionInputsGreatEqualThanOutputsRight(Transaction transaction) {
        long inputsValue = TransactionTool.getInputsValue(transaction);
        long outputsValue = TransactionTool.getOutputsValue(transaction);
        if(inputsValue < outputsValue) {
            logger.debug("交易校验失败：交易的输入必须大于等于交易的输出。不合法的交易。");
            return false;
        }
        return true;
    }

    public static UnspendTransactionOutput transactionOutput2UnspendTransactionOutput(TransactionOutput transactionOutput) {
        //UnspendTransactionOutput是TransactionOutput子类，且没有其它的属性才可以这样转换。
        String json = new Gson().toJson(transactionOutput);
        UnspendTransactionOutput unspendTransactionOutput = new Gson().fromJson(json,UnspendTransactionOutput.class);
        return unspendTransactionOutput;
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
        long transactionInputCount = getTransactionInputCount(transaction);
        long transactionOutputCount = getTransactionOutputCount(transaction);
        if(transactionInputCount <= 0){
            return 0;
        }
        return transactionInputCount-transactionOutputCount;
    }
}
