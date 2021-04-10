package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;
import com.xingkaichun.helloworldblockchain.util.LevelDBUtil;

/**
 * 区块链数据库主键工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainDatabaseKeyTool {
    //区块链高度key：它对应的值是区块链的高度
    private static final String BLOCKCHAIN_HEIGHT_KEY = "A";
    //区块链中总的交易数量
    private static final String BLOCKCHAIN_TRANSACTION_COUNT_KEY = "B";

    //哈希标识：哈希(交易哈希、交易输出哈希)的前缀，这里希望系统中所有使用到的哈希都是不同的
    private static final String HASH_PREFIX_FLAG = "C";

    //区块链中的交易序列号
    private static final String TRANSACTION_INDEX_IN_BLOCKCHAIN_TO_TRANSACTION_PREFIX_FLAG = "D";
    //区块高度标识：存储区块链高度到区块的映射
    private static final String BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG = "E";
    //标识：存储区块Hash到区块高度的映射
    private static final String BLOCK_HASH_TO_BLOCK_HEIGHT_PREFIX_FLAG = "G";
    //交易标识：存储交易哈希到交易的映射
    private static final String TRANSACTION_HASH_TO_TRANSACTION_PREFIX_FLAG = "H";
    //交易输出标识：存储交易输出哈希到交易输出的映射
    private static final String TRANSACTION_OUTPUT_HASH_TO_TRANSACTION_OUTPUT_PREFIX_FLAG = "I";
    //未花费的交易输出标识：存储未花费交易输出ID到未花费交易输出的映射
    private static final String UNSPEND_TRANSACTION_OUTPUT_ID_TO_UNSPEND_TRANSACTION_OUTPUT_PREFIX_FLAG = "J";
    //花费的交易输出标识：存储已经花费交易输出ID到花费所在的交易哈希的映射
    private static final String TRANSACTION_OUTPUT_ID_TO_TO_TRANSACTION_HASH_PREFIX_FLAG = "K";
    //地址标识：存储地址到交易输出的映射
    private static final String ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "L";
    //地址标识：存储地址到未花费交易输出的映射
    private static final String ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "M";
    //地址标识：存储地址到已花费交易输出的映射
    private static final String ADDRESS_TO_SPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG = "N";
    //地址标识：存储地址到交易哈希列表的映射
    private static final String ADDRESS_TO_TRANSACTION_HASH_LIST_KEY_PREFIX_FLAG = "O";
    //地址标识
    private static final String ADDRESS_PREFIX_FLAG = "P";

    //钱包地址截止标记
    private static final String ADDRESS_END_FLAG = "#" ;
    private static final String END_FLAG = "#" ;




    //拼装数据库Key的值
    public static byte[] buildBlockchainHeightKey() {
        String stringKey = BLOCKCHAIN_HEIGHT_KEY + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildHashKey(String hash) {
        String stringKey = HASH_PREFIX_FLAG + hash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressKey(String address) {
        String stringKey = ADDRESS_PREFIX_FLAG + address + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockHeightToBlockKey(long blockHeight) {
        String stringKey = BLOCK_HEIGHT_TO_BLOCK_PREFIX_FLAG + blockHeight + END_FLAG;
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
    public static byte[] buildTransactionOutputIdToTransactionOutputKey(TransactionOutputId transactionOutputId) {
        String stringKey = TRANSACTION_OUTPUT_HASH_TO_TRANSACTION_OUTPUT_PREFIX_FLAG + transactionOutputId.getTransactionOutputId() + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildUnspendTransactionOutputIdToUnspendTransactionOutputKey(TransactionOutputId transactionOutputId) {
        String stringKey = UNSPEND_TRANSACTION_OUTPUT_ID_TO_UNSPEND_TRANSACTION_OUTPUT_PREFIX_FLAG + transactionOutputId.getTransactionOutputId() + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionOutputIdToToTransactionHashKey(TransactionOutputId transactionOutputId) {
        String stringKey = TRANSACTION_OUTPUT_ID_TO_TO_TRANSACTION_HASH_PREFIX_FLAG + transactionOutputId.getTransactionOutputId() + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToTransactionOutputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getAddress();
        String transactionOutputId = transactionOutput.getTransactionOutputId();
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputId + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToTransactionOutputListKey(String address) {
        String stringKey = ADDRESS_TO_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToUnspendTransactionOutputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getAddress();
        String transactionOutputId = transactionOutput.getTransactionOutputId();
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputId + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToUnspendTransactionOutputListKey(String address) {
        String stringKey = ADDRESS_TO_UNSPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToSpendTransactionOutputListKey(TransactionOutput transactionOutput) {
        String address = transactionOutput.getAddress();
        String transactionOutputId = transactionOutput.getTransactionOutputId();
        String stringKey = ADDRESS_TO_SPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionOutputId + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToSpendTransactionOutputListKey(String address) {
        String stringKey = ADDRESS_TO_SPEND_TRANSACTION_OUTPUT_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildBlockchainTransactionCountKey() {
        String stringKey = BLOCKCHAIN_TRANSACTION_COUNT_KEY + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildTransactionIndexInBlockchainToTransactionKey(long transactionIndexInBlockchain) {
        String stringKey = TRANSACTION_INDEX_IN_BLOCKCHAIN_TO_TRANSACTION_PREFIX_FLAG + transactionIndexInBlockchain + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }
    public static byte[] buildAddressToTransactionHashListKey(String address, String transactionHash) {
        String stringKey = ADDRESS_TO_TRANSACTION_HASH_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG + transactionHash + END_FLAG;
        return LevelDBUtil.stringToBytes(stringKey);
    }

    public static byte[] buildAddressToTransactionHashListKey(String address) {
        String stringKey = ADDRESS_TO_TRANSACTION_HASH_LIST_KEY_PREFIX_FLAG + address + ADDRESS_END_FLAG ;
        return LevelDBUtil.stringToBytes(stringKey);
    }
}