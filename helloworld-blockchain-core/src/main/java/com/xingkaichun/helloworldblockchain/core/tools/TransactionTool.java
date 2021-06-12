package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.VirtualMachine;
import com.xingkaichun.helloworldblockchain.core.impl.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.script.*;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.util.ListUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaction工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class TransactionTool {

    /**
     * 交易输入总额
     */
    public static long getInputValue(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
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
    public static long getOutputValue(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        long total = 0;
        if(outputs != null){
            for(TransactionOutput output : outputs) {
                total += output.getValue();
            }
        }
        return total;
    }


    /**
     * 交易手续费（只计算标准交易的手续费，创世交易抛出异常）
     */
    public static long getTransactionFee(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.STANDARD){
            long fee = getInputValue(transaction) - getOutputValue(transaction);
            return fee;
        }else {
            throw new RuntimeException("只能计算标准交易类型的手续费");
        }
    }
    /**
     * 交易费率（只计算标准交易的手续费，创世交易抛出异常）
     */
    public static long getTransactionFeeRate(Transaction transaction) {
        if(transaction.getTransactionType() == TransactionType.STANDARD){
            return getTransactionFee(transaction)/SizeTool.calculateTransactionSize(transaction);
        }else {
            throw new RuntimeException("只能计算标准交易类型的交易费率");
        }
    }


    /**
     * 获取待签名数据
     */
    public static String signatureHashAll(Transaction transaction) {
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return TransactionDtoTool.signatureHashAll(transactionDto);
    }

    /**
     * 交易签名
     */
    public static String signature(String privateKey, Transaction transaction) {
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return TransactionDtoTool.signature(privateKey,transactionDto);
    }




    /**
     * 校验用户花费的是自己的钱吗，用户只可以花费自己的钱。专业点的说法，校验UTXO所有权，用户只可以花费自己拥有的UTXO。
     * 用户如何能证明自己拥有这个UTXO，只要用户能创建出一个能解锁(该UTXO对应的交易输出脚本)的交易输入脚本，就证明了用户拥有该UTXO。
     * 这是因为锁(交易输出脚本)是用户创建的，自然只有该用户有对应的钥匙(交易输入脚本)，自然意味着有钥匙的用户拥有这把锁的所有权。
     */
    public static boolean checkUtxoOwnership(Transaction transaction) {
        try{
            List<TransactionInput> inputs = transaction.getInputs();
            if(inputs != null && inputs.size()!=0){
                for(TransactionInput transactionInput:inputs){
                    //锁(交易输出脚本)
                    OutputScript outputScript = transactionInput.getUnspentTransactionOutput().getOutputScript();
                    //钥匙(交易输入脚本)
                    InputScript inputScript = transactionInput.getInputScript();
                    //完整脚本
                    Script script = ScriptTool.createScript(inputScript,outputScript);
                    VirtualMachine virtualMachine = new StackBasedVirtualMachine();
                    //执行脚本
                    ScriptExecuteResult scriptExecuteResult = virtualMachine.executeScript(transaction,script);
                    //脚本执行结果是个栈，如果栈有且只有一个元素，且这个元素是0x01，则解锁成功。
                    boolean executeSuccess = scriptExecuteResult.size()==1 && ByteUtil.equals(BooleanEnum.TRUE.getCode(),HexUtil.hexStringToBytes(scriptExecuteResult.pop()));
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
        TransactionDto transactionDto = Model2DtoTool.transaction2TransactionDto(transaction);
        return TransactionDtoTool.calculateTransactionHash(transactionDto);
    }






    /**
     * 交易中的金额是否符合系统的约束
     */
    public static boolean checkTransactionValue(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            //校验交易输入的金额
            for(TransactionInput input:inputs){
                if(!checkTransactionValue(input.getUnspentTransactionOutput().getValue())){
                    LogUtil.debug("交易金额不合法");
                    return false;
                }
            }
        }

        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            //校验交易输出的金额
            for(TransactionOutput output:outputs){
                if(!checkTransactionValue(output.getValue())){
                    LogUtil.debug("交易金额不合法");
                    return false;
                }
            }
        }

        //根据交易类型，做进一步的校验
        if(transaction.getTransactionType() == TransactionType.GENESIS){
            //没有需要校验的，跳过。
        } else if(transaction.getTransactionType() == TransactionType.STANDARD){
            //交易输入必须要大于等于交易输出
            long inputsValue = TransactionTool.getInputValue(transaction);
            long outputsValue = TransactionTool.getOutputValue(transaction);
            if(inputsValue < outputsValue) {
                LogUtil.debug("交易校验失败：交易的输入必须大于等于交易的输出。不合法的交易。");
                return false;
            }
            return true;
        } else {
            LogUtil.debug("区块数据异常，不能识别的交易类型。");
            return false;
        }
        return true;
    }

    /**
     * 校验交易中的地址是否是P2PKH地址
     */
    public static boolean checkPayToPublicKeyHashAddress(Transaction transaction) {
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                if(!AccountUtil.isPayToPublicKeyHashAddress(output.getAddress())){
                    LogUtil.debug("交易地址不合法");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 校验交易金额是否是一个合法的交易金额：这里用于限制交易金额的最大值、最小值、小数保留位等
     */
    public static boolean checkTransactionValue(long transactionAmount) {
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
     * 交易中是否存在重复的[未花费交易输出]
     */
    public static boolean isExistDuplicateUtxo(Transaction transaction) {
        List<String> utxoIdList = new ArrayList<>();
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput transactionInput : inputs) {
                TransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                String utxoId = getTransactionOutputId(unspentTransactionOutput);
                utxoIdList.add(utxoId);
            }
        }
        return ListUtil.isExistDuplicateElement(utxoIdList);
    }
    /**
     * 区块新产生的地址是否存在重复
     */
    public static boolean isExistDuplicateNewAddress(Transaction transaction) {
        List<String> newAddressList = new ArrayList<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs){
                String address = output.getAddress();
                newAddressList.add(address);
            }
        }
        return ListUtil.isExistDuplicateElement(newAddressList);
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
            long inputsValue = getInputValue(transaction);
            long outputsValue = getOutputValue(transaction);
            return inputsValue - outputsValue;
        }else {
            throw new RuntimeException("没有该交易类型。");
        }
    }

    /**
     * 按照费率(每字符的手续费)从大到小排序交易
     */
    public static void sortByTransactionFeeRateDescend(List<Transaction> transactions) {
        if(transactions == null){
            return;
        }
        transactions.sort((transaction1, transaction2) -> {
            long transaction1FeeRate = TransactionTool.getTransactionFeeRate(transaction1);
            long transaction2FeeRate = TransactionTool.getTransactionFeeRate(transaction2);
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

    /**
     * 校验交易中的脚本是否是P2PKH脚本
     */
    public static boolean checkPayToPublicKeyHashScript(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput input:inputs){
                if(!ScriptTool.isPayToPublicKeyHashInputScript(input.getInputScript())){
                    return false;
                }
            }
        }
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for (TransactionOutput output:outputs) {
                if(!ScriptTool.isPayToPublicKeyHashOutputScript(output.getOutputScript())){
                    return false;
                }
            }
        }
        return true;
    }

    public static String getTransactionOutputId(TransactionOutput transactionOutput) {
        return BlockchainDatabaseKeyTool.buildTransactionOutputId(transactionOutput.getTransactionHash(),transactionOutput.getTransactionOutputIndex());
    }
}
