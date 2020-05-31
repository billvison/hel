package com.xingkaichun.helloworldblockchain.core.utils;

import com.xingkaichun.helloworldblockchain.core.setting.GlobalSetting;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;

/**
 * LevelDB工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class LevelDBUtil {

    private static WriteOptions writeOptions = new WriteOptions();

    static {
        writeOptions.sync(true);
        writeOptions.snapshot(true);
    }

    public static void put(DB db, String key, byte[] bytesValue) throws DBException {
        put(db,stringToBytes(key),bytesValue);
    }

    public static void put(DB db, byte[] bytesKey, byte[] bytesValue) throws DBException {
        db.put(bytesKey,bytesValue);
    }

    public static void write(DB db, WriteBatch writeBatch) throws DBException {
        db.write(writeBatch);
    }

    public static byte[] get(DB db, String key) throws DBException {
        return get(db,stringToBytes(key));
    }

    public static byte[] get(DB db, byte[] bytesKey) throws DBException {
        return db.get(bytesKey);
    }

    public static void delete(DB db,String key) throws DBException {
        delete(db,stringToBytes(key));
    }

    public static void delete(DB db,byte[] bytesKey) throws DBException {
        db.delete(bytesKey);
    }

    public static DB createDB(File dbFile) throws Exception{
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        return factory.open(dbFile, options);
    }

    public static byte[] stringToBytes(String strValue) {
        return strValue.getBytes(GlobalSetting.GLOBAL_CHARSET);
    }

    public static String bytesToString(byte[] bytesValue) {
        return new String(bytesValue, GlobalSetting.GLOBAL_CHARSET);
    }
}
