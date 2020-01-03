package com.xingkaichun.blockchain.core.utils.atomic;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.nio.charset.Charset;

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

    public static void put(DB db,WriteBatch writeBatch) throws DBException{
        db.write(writeBatch);
    }

    public static byte[] get(DB db,String key) throws DBException{
        return db.get(stringToBytes(key));
    }

    public static void delete(DB db,String key) throws DBException{
        db.delete(stringToBytes(key));
    }

    public static DB createDB(File dbFile) throws Exception{
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        return factory.open(dbFile, options);
    }

    public static byte[] stringToBytes(long value) {
        String strValue = String.valueOf(value);
        return stringToBytes(strValue);
    }

    public static byte[] stringToBytes(String value) {
        return value.getBytes(CHARSET_UTF_8);
    }
}
