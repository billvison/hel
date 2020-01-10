package com.xingkaichun.blockchain.core.utils.atomic;

import java.io.File;

public class FileUtils {

    public static boolean exists(String pathname) {
        File file = new File(pathname);
        return file.exists();
    }

    public static boolean exists(String parent, String child) {
        File file = new File(parent,child);
        return file.exists();
    }
}
