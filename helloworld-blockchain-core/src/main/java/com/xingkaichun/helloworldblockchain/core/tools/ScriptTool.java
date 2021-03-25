package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.primitives.Bytes;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.InputScriptDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.OutputScriptDTO;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 脚本工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ScriptTool {

    //region 序列化与反序列化
    /**
     * 字节型脚本：将脚本序列化，要求序列化后的脚本可以反序列化。
     */
    public static byte[] bytesScript(List<String> script) {
        byte[] bytesScript = new byte[0];
        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                bytesScript = Bytes.concat(bytesScript, ByteUtil.concatLengthBytes(bytesOperationCode));
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA1024.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                byte[] bytesOperationData = HexUtil.hexStringToBytes(operationData);
                bytesScript = Bytes.concat(bytesScript, ByteUtil.concatLengthBytes(bytesOperationCode), ByteUtil.concatLengthBytes(bytesOperationData));
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return bytesScript;
    }
    /**
     * 脚本：将字节型脚本反序列化为脚本.
     */
    public static InputScriptDTO inputScriptDTO(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        InputScriptDTO inputScriptDTO = new InputScriptDTO();
        List<String> script = script(bytesScript);
        inputScriptDTO.addAll(script);
        return inputScriptDTO;
    }
    /**
     * 脚本：将字节型脚本反序列化为脚本.
     */
    public static OutputScriptDTO outputScriptDTO(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        OutputScriptDTO outputScriptDTO = new OutputScriptDTO();
        List<String> script = script(bytesScript);
        outputScriptDTO.addAll(script);
        return outputScriptDTO;
    }
    /**
     * 脚本：将字节型脚本反序列化为脚本.
     */
    private static List<String> script(byte[] bytesScript) {
        if(bytesScript == null || bytesScript.length == 0){
            return null;
        }
        int start = 0;
        List<String> script = new ArrayList<>();
        while (start<bytesScript.length){
            long bytesOperationCodeLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesScript,start,start+8));
            start += 8;
            byte[] bytesOperationCode = Arrays.copyOfRange(bytesScript,start, start+(int) bytesOperationCodeLength);
            start += bytesOperationCodeLength;
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                String stringOperationCode = HexUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA1024.getCode(),bytesOperationCode)){
                String stringOperationCode = HexUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);

                long bytesOperationDataLength = ByteUtil.bytes8BigEndianToLong(Arrays.copyOfRange(bytesScript,start,start+8));
                start += 8;
                byte[] bytesOperationData = Arrays.copyOfRange(bytesScript,start, start+(int) bytesOperationDataLength);
                start += bytesOperationDataLength;
                String stringOperationData = HexUtil.bytesToHexString(bytesOperationData);
                script.add(stringOperationData);
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return script;
    }
    //endregion




    /**
     * 可视、可阅读的脚本，区块链浏览器使用
     */
    public static String toString(List<String> script) {
        String stringScript = "";
        for(int i=0;i<script.size();i++){
            String operationCode = script.get(i);
            byte[] bytesOperationCode = HexUtil.hexStringToBytes(operationCode);
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_DUP.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_HASH160.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_EQUALVERIFY.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                stringScript = stringScript + OperationCodeEnum.OP_CHECKSIG.getName() + " ";
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA1024.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                stringScript = stringScript + OperationCodeEnum.OP_PUSHDATA1024.getName() + " ";
                stringScript = stringScript + operationData + " ";
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return stringScript;
    }
}
