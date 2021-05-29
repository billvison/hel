package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.InputScriptDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.OutputScriptDto;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;


public class ScriptToolTest {

    @Test
    public void bytesScriptTest()
    {
        InputScriptDto inputScriptDTO = new InputScriptDto();
        inputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        inputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");

        InputScriptDto resumeInputScriptDto =  ScriptTool.inputScriptDTO(ScriptTool.bytesScript(inputScriptDTO));
        Assert.assertEquals(JsonUtil.toJson(inputScriptDTO),JsonUtil.toJson(resumeInputScriptDto));



        OutputScriptDto outputScriptDTO = new OutputScriptDto();
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        outputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));

        OutputScriptDto resumeOutputScriptDto =  ScriptTool.outputScriptDTO(ScriptTool.bytesScript(outputScriptDTO));
        Assert.assertEquals(JsonUtil.toJson(outputScriptDTO),JsonUtil.toJson(resumeOutputScriptDto));
    }
}
