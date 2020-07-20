package com.xingkaichun.helloworldblockchain.core.utils;

import java.io.File;

public class FileUtil {

    public static void mkdir(String path) {
        new File(path).mkdirs();
    }
}
