package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.Script;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.NumberUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.InputScriptDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.OutputScriptDTO;

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
                bytesScript = ByteUtil.concat(bytesScript, ByteUtil.concatLength(bytesOperationCode));
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                byte[] bytesOperationData = HexUtil.hexStringToBytes(operationData);
                bytesScript = ByteUtil.concat(bytesScript, ByteUtil.concatLength(bytesOperationCode), ByteUtil.concatLength(bytesOperationData));
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
            long bytesOperationCodeLength = NumberUtil.byte8ToLong8(Arrays.copyOfRange(bytesScript,start,start+8));
            start += 8;
            byte[] bytesOperationCode = Arrays.copyOfRange(bytesScript,start, start+(int) bytesOperationCodeLength);
            start += bytesOperationCodeLength;
            if(Arrays.equals(OperationCodeEnum.OP_DUP.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_HASH160.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_EQUALVERIFY.getCode(),bytesOperationCode) ||
                    Arrays.equals(OperationCodeEnum.OP_CHECKSIG.getCode(),bytesOperationCode)){
                String stringOperationCode = HexUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String stringOperationCode = HexUtil.bytesToHexString(bytesOperationCode);
                script.add(stringOperationCode);

                long bytesOperationDataLength = NumberUtil.byte8ToLong8(Arrays.copyOfRange(bytesScript,start,start+8));
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
            }else if(Arrays.equals(OperationCodeEnum.OP_PUSHDATA.getCode(),bytesOperationCode)){
                String operationData = script.get(++i);
                stringScript = stringScript + OperationCodeEnum.OP_PUSHDATA.getName() + " ";
                stringScript = stringScript + operationData + " ";
            }else {
                throw new RuntimeException("不能识别的指令");
            }
        }
        return stringScript;
    }

    /**
     * 构建完整脚本
     */
    public static Script createScript(InputScript inputScript, OutputScript outputScript) {
        Script script = new Script();
        script.addAll(inputScript);
        script.addAll(outputScript);
        return script;
    }

    /**
     * 创建P2PKH输入脚本
     */
    public static InputScript createPayToPublicKeyHashInputScript(String sign, String publicKey) {
        InputScript script = new InputScript();
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        script.add(sign);
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        script.add(publicKey);
        return script;
    }

    /**
     * 创建P2PKH输出脚本
     */
    public static OutputScript createPayToPublicKeyHashOutputScript(String address) {
        OutputScript script = new OutputScript();
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        String publicKeyHash = AccountUtil.publicKeyHashFromAddress(address);
        script.add(publicKeyHash);
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        script.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        return script;
    }

    /**
     * 是否是P2PKH输入脚本
     */
    public static boolean isPayToPublicKeyHashInputScript(InputScriptDTO inputScriptDTO) {
        try {
            return  inputScriptDTO.size() == 4
                    && HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()).equals(inputScriptDTO.get(0))
                    && (136 <= inputScriptDTO.get(1).length() && 144 >= inputScriptDTO.get(1).length())
                    && HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()).equals(inputScriptDTO.get(2))
                    && 130 == inputScriptDTO.get(3).length();
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 是否是P2PKH输出脚本
     */
    public static boolean isPayToPublicKeyHashOutputScript(OutputScriptDTO outputScriptDTO) {
        try {
            return  outputScriptDTO.size() == 6
                    && HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()).equals(outputScriptDTO.get(0))
                    && HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()).equals(outputScriptDTO.get(1))
                    && HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()).equals(outputScriptDTO.get(2))
                    && 40 == outputScriptDTO.get(3).length()
                    && HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()).equals(outputScriptDTO.get(4))
                    && HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()).equals(outputScriptDTO.get(5));
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 获取P2PKH脚本中的公钥哈希
     */
    public static String getPublicKeyHashByPayToPublicKeyHashOutputScript(List<String> outputScript) {
        return outputScript.get(3);
    }
}
