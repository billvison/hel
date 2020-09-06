package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.utils.LevelDBUtil;

/**
 * 区块链数据库主键工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockChainDataBaseKeyTool {
    //区块链高度key：它对应的值是区块链的高度
    private final static String BLOCK_CHAIN_HEIGHT_KEY = "A";
    //区块链中总的交易数量
    private final static String TOTAL_TRANSACTION_QUANTITY_KEY = "B";

    //哈希标识：哈希(交易哈希、交易输出哈希)的前缀，这里希望系统中所有使用到的哈希都是不同的
    private final static String HASH_PREFIX_FLAG = "C";

    //区块链中的交易序列号
    private final static String TRANSACTION_SEQUENCE_NUMBER_IN_BLOCKCHAIN_TO_TRANSACTION_PREFIX_FLAG = "D";
    //区块高度标识：存储区块链高度到区块的映射
    private final static String BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG = "E";
    //区块高度标识：存储区块链高度到没有交易信息的区块的映射
    private final static String BLOCK_HEIGHT_TO_NO_TRANSACTION_BLOCK_PREFIX_FLAG = "F";
    //标识：存储区块Hash到区块高度的映射
    private final static String BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG = "G";
    //交易标识：存储交易哈希到交易的映射
    private final static String TRANSACTION_HASH_TO_TRANSACTION_PREFIX_FLAG = "H";
    //交易输出标识：存储交易输出哈希到交易输出的映射
    private final static String TRANSACTION_OUTPUT_HASH_TO_TRANSACTION_OUTPUT_PREFIX_FLAG = "I";
    //未花费的交易输出标识：存储未花费交易输出哈希到未花费交易输出的映射
    private final static String UNSPEND_TRANSACTION_OUTPUT_HASH_TO_UNSPEND_TRANSACTION_OUTPUT_PREFIX_FLAG = "J";
    //地址标识：存储地址到交易输出的映射
    private final static String ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "K";
    //地址标识：存储地址到未花费交易输出的映射
    private final static String ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "L";

    //钱包地址截止标记
    private final static String ADDRESS_END_FLAG = "#" ;
    private final static String END_FLAG = "#" ;




    //拼装数据库Key的值
    public static byte[] buildBlockChainHeightKey() {
        String stringKey = BLOCK_CHAIN_HEIGHT_KEY + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildHashKey(String hash) {
        String stringKey = HASH_PREFIX_FLAG + hash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockHeightToBlockKey(long blockHeight) {
        String stringKey = BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG + blockHeight + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockHeightToNoTransactionBlockKey(long blockHeight) {
        String stringKey = BLOCK_HEIGHT_TO_NO_TRANSACTION_BLOCK_PREFIX_FLAG + blockHeight + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockHashToBlockHeightKey(String blockHash) {
        String stringKey = BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG + blockHash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionHashToTransactionKey(String transactionHash) {
        String stringKey = TRANSACTION_HASH_TO_TRANSACTION_PREFIX_FLAG + transactionHash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionOutputHashToTransactionOutputKey(String transactionOutputHash) {
        String stringKey = TRANSACTION_OUTPUT_HASH_TO_TRANSACTION_OUTPUT_PREFIX_FLAG + transactionOutputHash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildUnspendTransactionOutputHashToUnspendTransactionOutputKey(String transactionOutputHash) {
        String stringKey = UNSPEND_TRANSACTION_OUTPUT_HASH_TO_UNSPEND_TRANSACTION_OUTPUT_PREFIX_FLAG + transactionOutputHash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToTransactionOuputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getAddress();
        String transactionOutputHash = transactionOutput.getTransactionOutputHash();
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputHash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToTransactionOuputListKey(String address) {
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToUnspendTransactionOutputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getAddress();
        String transactionOutputHash = transactionOutput.getTransactionOutputHash();
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputHash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToUnspendTransactionOutputListKey(String address) {
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTotalTransactionQuantityKey() {
        String stringKey = TOTAL_TRANSACTION_QUANTITY_KEY + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionSequenceNumberInBlockChainToTransactionKey(long transactionSequenceNumberInBlockChain) {
        String stringKey = TRANSACTION_SEQUENCE_NUMBER_IN_BLOCKCHAIN_TO_TRANSACTION_PREFIX_FLAG + transactionSequenceNumberInBlockChain + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
}