package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.nio.charset.Charset;

/**
 * LevelDB工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class LevelDBUtil {

    private static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    private static WriteOptions writeOptions = new WriteOptions();

    static {
        writeOptions.sync(true);
        writeOptions.snapshot(true);
    }

    public static void put(DB db, String key, byte[] bytesValue) throws DBException{
        db.put(stringToBytes(key),bytesValue);
    }

    public static void put(DB db, byte[] bytesKey, byte[] bytesValue) throws DBException{
        db.put(bytesKey,bytesValue);
    }

    public static void write(DB db, WriteBatch writeBatch) throws DBException{
        db.write(writeBatch);
    }

    public static byte[] get(DB db,String key) throws DBException{
        return db.get(stringToBytes(key));
    }

    public static byte[] get(DB db, byte[] byteskey) throws DBException{
        return db.get(byteskey);
    }

    public static void delete(DB db,String key) throws DBException, InterruptedException {
        db.delete(stringToBytes(key));
    }

    public static void delete(DB db,byte[] byteskey) throws DBException, InterruptedException {
        db.delete(byteskey);
    }

    public static DB createDB(File dbFile) throws Exception{
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        return factory.open(dbFile, options);
    }

    public static byte[] stringToBytes(String strValue) {
        return strValue.getBytes(CHARSET_UTF_8);
    }

    public static String bytesToString(byte[] bytesValue) {
        return new String(bytesValue,CHARSET_UTF_8);
    }
}
