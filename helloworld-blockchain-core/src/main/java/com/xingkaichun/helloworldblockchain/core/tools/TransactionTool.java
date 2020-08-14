package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.base.Joiner;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.core.model.script.ScriptExecuteResult;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.script.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.utils.NumberUtil;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class TransactionTool {

    private final static Logger logger = LoggerFactory.getLogger(TransactionTool.class);

    /**
     * 交易输入总额
     */
    public static BigDecimal getInputsValue(Transaction transaction) {
        return getInputsValue(transaction.getInputs());
    }
    /**
     * 交易输入总额
     */
    public static BigDecimal getInputsValue(List<TransactionInput> inputs) {
        BigDecimal total = BigDecimal.ZERO;
        if(inputs != null){
            for(TransactionInput i : inputs) {
                if(i.getUnspendTransactionOutput() == null) continue;
                total = total.add(i.getUnspendTransactionOutput().getValue());
            }
        }
        return total;
    }



    /**
     * 交易输出总额
     */
    public static BigDecimal getOutputsValue(Transaction transaction) {
        return getOutputsValue(transaction.getOutputs());
    }
    /**
     * 交易输出总额
     */
    public static BigDecimal getOutputsValue(List<TransactionOutput> outputs) {
        BigDecimal total = BigDecimal.ZERO;
        if(outputs != null){
            for(TransactionOutput o : outputs) {
                total = total.add(o.getValue());
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
                return Boolean.valueOf(scriptExecuteResult.pop());
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
        for(TransactionOutputDTO transactionOutputDTO:outputs){
            outputHashList.add(calculateTransactionOutputHash(transactionDTO,transactionOutputDTO));
        }
        return calculateTransactionHash(transactionDTO.getTimestamp(),inputHashList,outputHashList);
    }

    /**
     * 计算交易哈希
     */
    private static String calculateTransactionHash(long currentTimeMillis,List<String> inputHashList,List<String> outputHashList){
        String data = "";
        if(inputHashList != null && inputHashList.size()!=0){
            data += "[" + Joiner.on(" ").join(inputHashList) + "]";
        }
        if(outputHashList != null && outputHashList.size()!=0){
            data += "[" + Joiner.on(" ").join(outputHashList) + "]";
        }
        byte[] sha256Digest = SHA256Util.digest(ByteUtil.stringToBytes(data));
        return HexUtil.bytesToHexString(sha256Digest) + currentTimeMillis;
    }

    /**
     * 计算交易输出哈希
     */
    public static String calculateTransactionOutputHash(Transaction transaction,TransactionOutput output) {
        return calculateTransactionOutputHash(transaction.getTimestamp(),output.getAddress(),output.getValue().toPlainString(),output.getScriptLock());
    }

    /**
     * 计算交易输出哈希
     */
    public static String calculateTransactionOutputHash(TransactionDTO transactionDTO,TransactionOutputDTO transactionOutputDTO) {
        return calculateTransactionOutputHash(transactionDTO.getTimestamp(),transactionOutputDTO.getAddress(),transactionOutputDTO.getValue(),transactionOutputDTO.getScriptLock());
    }

    /**
     * 计算交易输出哈希
     */
    private static String calculateTransactionOutputHash(long currentTimeMillis, String address, String value, List<String> scriptLock) {
        String forHash = "";
        forHash += "[" + address + "]";
        forHash += "[" + value + "]";
        forHash += "[" + Joiner.on(" ").join(scriptLock) + "]";
        byte[] sha256Digest = SHA256Util.digest(ByteUtil.stringToBytes(forHash));
        return HexUtil.bytesToHexString(sha256Digest) + + currentTimeMillis;
    }


    /**
     * 交易中的金额是否符合系统的约束
     */
    public static boolean isTransactionAmountLegal(Transaction transaction) {
        //TODO 交易输入>交易输出
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
    public static boolean isTransactionAmountLegal(BigDecimal transactionAmount) {
        try {
            if(transactionAmount == null){
                logger.debug("交易金额不合法：交易金额不能为空");
                return false;
            }
            //校验交易金额最小值
            if(transactionAmount.compareTo(GlobalSetting.TransactionConstant.TRANSACTION_MIN_AMOUNT) < 0){
                logger.debug("交易金额不合法：交易金额不能小于系统默认交易金额最小值");
                return false;
            }
            //校验交易金额最大值
            if(transactionAmount.compareTo(GlobalSetting.TransactionConstant.TRANSACTION_MAX_AMOUNT) > 0){
                logger.debug("交易金额不合法：交易金额不能大于系统默认交易金额最大值");
                return false;
            }
            //校验小数位数
            long decimalPlaces = NumberUtil.obtainDecimalPlaces(transactionAmount);
            if(decimalPlaces > GlobalSetting.TransactionConstant.TRANSACTION_AMOUNT_MAX_DECIMAL_PLACES){
                logger.debug("交易金额不合法：交易金额的小数位数过多，大于系统默认小说最高精度");
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
    public static boolean isIncentiveRight(BigDecimal targetMinerReward, Transaction transaction) {
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
        if(targetMinerReward.compareTo(outputs.get(0).getValue())>=0){
            logger.debug("挖矿奖励数据异常，挖矿奖励金额大于应该获得奖励金额。");
            return false;
        }
        return true;
    }
}
