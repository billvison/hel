package com.xingkaichun.helloworldblockchain.netcore.util;

import java.io.File;

public class FileUtil {

    public static void mkdir(String path) {
        new File(path).mkdirs();
    }
}
