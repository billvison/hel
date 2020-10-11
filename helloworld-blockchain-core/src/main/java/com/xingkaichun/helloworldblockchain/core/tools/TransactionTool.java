package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.base.Joiner;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.script.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionOutputDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaction工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
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
            for(TransactionInput i : inputs) {
                if(i.getUnspendTransactionOutput() == null) continue;
                total += i.getUnspendTransactionOutput().getValue();
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
     * 获取用于签名的交易数据
     */
    public static String getSignatureData(Transaction transaction) {
        String data = transaction.getTransactionHash();
        return data;
    }

    /**
     * 交易签名
     */
    public static String signature(String privateKey, Transaction transaction) {
        String strSignature = AccountUtil.signature(privateKey, getSignatureData(transaction));
        return strSignature;
    }

    /**
     * 验证脚本
     */
    public static boolean verifyScript(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInput transactionInput:inputs){
                Script payToClassicAddressScript = StackBasedVirtualMachine.createPayToClassicAddressScript(transactionInput.getScriptKey(),transactionInput.getUnspendTransactionOutput().getScriptLock());
                StackBasedVirtualMachine stackBasedVirtualMachine = new StackBasedVirtualMachine();
                ScriptExecuteResult scriptExecuteResult = stackBasedVirtualMachine.executeScript(transaction,payToClassicAddressScript);
                if(scriptExecuteResult.size()!=1 || !Boolean.valueOf(scriptExecuteResult.pop())){
                    return false;
                }
            }
        }
        return true;
    }




    /**
     * 校验交易的哈希是否正确
     */
    public static boolean isTransactionHashRight(Transaction transaction) {
        String transactionHash = transaction.getTransactionHash();
        String targetTransactionHash = calculateTransactionHash(transaction);
        return transactionHash.equals(targetTransactionHash);
    }

    /**
     * 校验交易输出的哈希是否正确
     */
    public static boolean isTransactionOutputHashRight(Transaction transaction,TransactionOutput output) {
        String transactionOutputHash = output.getTransactionOutputHash();
        String targetTransactionOutputHash = calculateTransactionOutputHash(transaction,output);
        return transactionOutputHash.equals(targetTransactionOutputHash);
    }

    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(Transaction transaction){
        List<String> inputHashList = new ArrayList<>();
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInput transactionInput:inputs){
                inputHashList.add(transactionInput.getUnspendTransactionOutput().getTransactionOutputHash());
            }
        }
        List<String> outputHashList = new ArrayList<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null && outputs.size()!=0){
            for(TransactionOutput transactionOutput:outputs){
                outputHashList.add(transactionOutput.getTransactionOutputHash());
            }
        }
        return calculateTransactionHash(transaction.getTimestamp(),inputHashList,outputHashList);
    }

    /**
     * 计算交易哈希
     */
    public static String calculateTransactionHash(TransactionDTO transactionDTO){
        List<String> inputHashList = new ArrayList<>();
        List<TransactionInputDTO> inputs = transactionDTO.getInputs();
        if(inputs != null && inputs.size()!=0){
            for(TransactionInputDTO transactionInputDTO:inputs){
                inputHashList.add(transactionInputDTO.getUnspendTransactionOutputHash());
            }
        }
        List<String> outputHashList = new ArrayList<>();
        List<TransactionOutputDTO> outputs = transactionDTO.getOutputs();
        long transactionOutputSequence = 0;
        for(TransactionOutputDTO transactionOutputDTO:outputs){
            transactionOutputSequence++;
            outputHashList.add(calculateTransactionOutputHash(transactionDTO.getTimestamp(),transactionOutputSequence,transactionOutputDTO));
        }
        return calculateTransactionHash(transactionDTO.getTimestamp(),inputHashList,outputHashList);
    }

    /**
     * 计算交易哈希
     */
    private static String calculateTransactionHash(long currentTimeMillis,List<String> inputHashList,List<String> outputHashList){
        String data = "[" + currentTimeMillis + "]";
        if(inputHashList != null && inputHashList.size()!=0){
            data += "[" + Joiner.on(" ").join(inputHashList) + "]";
        }
        if(outputHashList != null && outputHashList.size()!=0){
            data += "[" + Joiner.on(" ").join(outputHashList) + "]";
        }
        byte[] sha256Digest = SHA256Util.digest(ByteUtil.stringToBytes(data));
        return HexUtil.bytesToHexString(sha256Digest);
    }

    /**
     * 计算交易输出哈希
     */
    public static String calculateTransactionOutputHash(Transaction transaction,TransactionOutput output) {
        return calculateTransactionOutputHash(transaction.getTimestamp(),output.getTransactionOutputSequence(),output.getValue(),output.getScriptLock());
    }

    /**
     * 计算交易输出哈希
     */
    public static String calculateTransactionOutputHash(long timestamp, long transactionOutputSequence, TransactionOutputDTO transactionOutputDTO) {
        return calculateTransactionOutputHash(timestamp,transactionOutputSequence,transactionOutputDTO.getValue(),transactionOutputDTO.getScriptLock());
    }

    /**
     * 计算交易输出哈希
     */
    private static String calculateTransactionOutputHash(long currentTimeMillis, long transactionOutputSequence, long value, List<String> scriptLock) {
        String forHash = "[" + currentTimeMillis + "]";
        forHash += "[" + transactionOutputSequence + "]";
        forHash += "[" + value + "]";
        forHash += "[" + Joiner.on(" ").join(scriptLock) + "]";
        byte[] sha256Digest = SHA256Util.digest(ByteUtil.stringToBytes(forHash));
        return HexUtil.bytesToHexString(sha256Digest);
    }


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
            //校验交易金额最小值
            if(transactionAmount < GlobalSetting.TransactionConstant.TRANSACTION_MIN_AMOUNT){
                logger.debug("交易金额不合法：交易金额不能小于系统默认交易金额最小值");
                return false;
            }
            //校验交易金额最大值
            if(transactionAmount > GlobalSetting.TransactionConstant.TRANSACTION_MAX_AMOUNT){
                logger.debug("交易金额不合法：交易金额不能大于系统默认交易金额最大值");
                return false;
            }
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
}
