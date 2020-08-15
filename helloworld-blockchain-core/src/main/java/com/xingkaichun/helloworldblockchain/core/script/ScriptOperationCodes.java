package com.xingkaichun.helloworldblockchain.core.script;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * 基于栈的虚拟机
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ScriptOperationCodes {

    //操作码前缀
    public static final String OPERATION_CODE_PREFIX = "0";
    //操作数前缀
    public static final String OPERATION_DATA_PREFIX = "1";

    /**
     * 将栈顶的元素复制一份，并放入栈。
     * OPERATION_CODE_DUPLICATE
     */
    public static final String OP_DUP = OPERATION_CODE_PREFIX + 0x76;
    public static final String OP_DUP_NAME = "OP_DUP";

    /**
     * 把栈顶元素当做公钥，求地址。然后删除栈顶元素，然后将地址放入栈。
     * OPERATION_CODE_PUBLIC_KEY_TO_CLASSIC_ADDRESS
     */
    public static final String OP_HASH160 = OPERATION_CODE_PREFIX + 0xa9;
    public static final String OP_HASH160_NAME = "OP_HASH160";

    /**
     * 比较栈顶的前两个元素是否相等，不等抛出异常。
     * 无论是否相等，最后从栈顶移除这两个元素。
     * OPERATION_CODE_EQUAL_VERIFY
     */
    public static final String OP_EQUALVERIFY = OPERATION_CODE_PREFIX + 0x88;
    public static final String OP_EQUALVERIFY_NAME = "OP_EQUALVERIFY";

    /**
     * 栈顶第一个元素是公钥
     * 栈顶第二个元素是交易签名
     * 通过公钥校验交易签名是否正确。
     * 如果校验成功，先从栈中移除这两个元素，然后将true放入栈。
     * 如果校验失败，抛出异常。
     * 无论如何，栈中一定要移除这两个元素。
     * OPERATION_CODE_CHECK_SIGN
     */
    public static final String OP_CHECKSIG = OPERATION_CODE_PREFIX + 0xac;
    public static final String OP_CHECKSIG_NAME = "OP_CHECKSIG";


    private static final Map<String, String> opCodeMap = ImmutableMap.<String, String>builder()
            .put(OP_DUP, OP_DUP_NAME)
            .put(OP_EQUALVERIFY, OP_EQUALVERIFY_NAME)
            .put(OP_HASH160, OP_HASH160_NAME)
            .put(OP_CHECKSIG, OP_CHECKSIG_NAME).build();

    private static final Map<String, String> opCodeNameMap = ImmutableMap.<String, String>builder()
            .put(OP_DUP_NAME, OP_DUP)
            .put(OP_EQUALVERIFY_NAME, OP_EQUALVERIFY)
            .put(OP_HASH160_NAME, OP_HASH160)
            .put(OP_CHECKSIG_NAME, OP_CHECKSIG).build();


    /**
     * 操作数移除操作数前缀，返回真实的操作数
     */
    public static String getDataFromOperationData(String operationData){
        return operationData.substring(ScriptOperationCodes.OPERATION_DATA_PREFIX.length());
    }

    /**
     * 将操作数前缀加到真实的操作数前面
     */
    public static String getOperationDataFromData(String data){
        return ScriptOperationCodes.OPERATION_DATA_PREFIX + data;
    }

    /**
     * 操作数移除操作数前缀，返回真实的操作数
     */
    public static String getCodeFromOperationCode(String operationCode){
        return operationCode.substring(ScriptOperationCodes.OPERATION_CODE_PREFIX.length());
    }

    /**
     * 返回编码名称或是真实数据
     */
    public static String getCodeNameOrRawData(String operation){
        if(operation.startsWith(OPERATION_DATA_PREFIX)){
            return getDataFromOperationData(operation);
        }
        return opCodeMap.get(operation);
    }
}
