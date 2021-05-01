package com.xingkaichun.helloworldblockchain.util;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * LevelDB工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class LevelDBUtil {

    private static final Logger logger = LoggerFactory.getLogger(LevelDBUtil.class);

    private static WriteOptions writeOptions = new WriteOptions();

    static {
        writeOptions.sync(true);
        writeOptions.snapshot(true);
    }

    public static void put(DB db, byte[] bytesKey, byte[] bytesValue) {
        db.put(bytesKey,bytesValue);
    }

    public static void write(DB db, WriteBatch writeBatch) {
        db.write(writeBatch);
    }

    public static byte[] get(DB db, byte[] bytesKey) {
        return db.get(bytesKey);
    }

    public static void delete(DB db,byte[] bytesKey) {
        db.delete(bytesKey);
    }

    public static DB createDB(File dbFile) {
        try {
            DBFactory factory = new Iq80DBFactory();
            Options options = new Options();
            return factory.open(dbFile, options);
        } catch (IOException e) {
            logger.error(String.format("create or load LevelDB database failed. LevelDB database file path is %s.",dbFile.getAbsolutePath()),e);
            throw new RuntimeException(e);
        }
    }

    public static void closeDB(DB dB){
        try {
            dB.close();
        } catch (Exception e) {
            logger.error("LevelDB database close failed.",e);
        }
    }

    public static byte[] stringToBytes(String strValue) {
        return strValue.getBytes(StandardCharsets.UTF_8);
    }

    public static String bytesToString(byte[] bytesValue) {
        return new String(bytesValue, StandardCharsets.UTF_8);
    }

    public static byte[] longToBytes(long longValue) {
        return stringToBytes(String.valueOf(longValue));
    }

    public static long bytesToLong(byte[] bytesValue) {
        return Long.parseLong(bytesToString(bytesValue));
    }
}
