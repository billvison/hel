package com.xingkaichun.blockchain.core.utils.atomic;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;

public class LevelDBUtil {
    private static WriteOptions writeOptions = new WriteOptions();
    static {
        writeOptions.sync(true);
        writeOptions.snapshot(true);
    }
    public static void put(DB db,WriteBatch writeBatch) throws DBException{
        db.write(writeBatch);
    }

    public static byte[] get(DB db,String key) throws DBException{
        return db.get(key.getBytes(BlockChainCoreConstants.CHARSET_UTF_8));
    }

    public static DB createDB(File dbFile) throws Exception{
        DBFactory factory = new Iq80DBFactory();
        Options options = new Options();
        return factory.open(dbFile, options);
    }
}
