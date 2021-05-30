package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

/**
 * EncodeDecode工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class EncodeDecodeTool {

    public static byte[] encode(Transaction transaction) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toJson(transaction));
        } catch (Exception e) {
            LogUtil.error("serialize Transaction failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static Transaction decodeToTransaction(byte[] bytesTransaction) {
        try {
            return JsonUtil.fromJson(ByteUtil.utf8BytesToString(bytesTransaction),Transaction.class);
        } catch (Exception e) {
            LogUtil.error("deserialize Transaction failed.",e);
            throw new RuntimeException(e);
        }
    }


    public static byte[] encode(TransactionOutput transactionOutput) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toJson(transactionOutput));
        } catch (Exception e) {
            LogUtil.error("serialize TransactionOutput failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static TransactionOutput decodeToTransactionOutput(byte[] bytesTransactionOutput) {
        try {
            return JsonUtil.fromJson(ByteUtil.utf8BytesToString(bytesTransactionOutput),TransactionOutput.class);
        } catch (Exception e) {
            LogUtil.error("deserialize TransactionOutput failed.",e);
            throw new RuntimeException(e);
        }
    }


    public static byte[] encode(Block block) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toJson(block));
        } catch (Exception e) {
            LogUtil.error("serialize Block failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static Block decodeToBlock(byte[] bytesBlock) {
        try {
            return JsonUtil.fromJson(ByteUtil.utf8BytesToString(bytesBlock),Block.class);
        } catch (Exception e) {
            LogUtil.error("deserialize Block failed.",e);
            throw new RuntimeException(e);
        }
    }



    public static byte[] encode(TransactionDto transactionDto) {
        try {
            return ByteUtil.stringToUtf8Bytes(JsonUtil.toJson(transactionDto));
        } catch (Exception e) {
            LogUtil.error("serialize TransactionDto failed.",e);
            throw new RuntimeException(e);
        }
    }
    public static TransactionDto decodeToTransactionDto(byte[] bytesTransactionDto) {
        try {
            return JsonUtil.fromJson(ByteUtil.utf8BytesToString(bytesTransactionDto), TransactionDto.class);
        } catch (Exception e) {
            LogUtil.error("deserialize TransactionDto failed.",e);
            throw new RuntimeException(e);
        }
    }
}
