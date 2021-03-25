package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.InputScriptDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.OutputScriptDTO;
import org.junit.Assert;
import org.junit.Test;


public class ScriptToolTest {

    @Test
    public void bytesScriptTest()
    {
        InputScriptDTO inputScriptDTO = new InputScriptDTO();
        inputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        inputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");

        InputScriptDTO resumeInputScriptDTO =  ScriptTool.inputScriptDTO(ScriptTool.bytesScript(inputScriptDTO));
        Assert.assertEquals(new Gson().toJson(inputScriptDTO),new Gson().toJson(resumeInputScriptDTO));



        OutputScriptDTO outputScriptDTO = new OutputScriptDTO();
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        outputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));

        OutputScriptDTO resumeOutputScriptDTO =  ScriptTool.outputScriptDTO(ScriptTool.bytesScript(outputScriptDTO));
        Assert.assertEquals(new Gson().toJson(outputScriptDTO),new Gson().toJson(resumeOutputScriptDTO));
    }
}
