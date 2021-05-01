package com.xingkaichun.helloworldblockchain.util;

import java.io.File;

/**
 * File工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class FileUtil {

    public static void mkdirs(String path) {
        mkdirs(new File(path));
    }

    public static void mkdirs(File file) {
        if(file.exists()){
            return;
        }
        boolean mkdirs = file.mkdirs();
        if(!mkdirs){
            throw new RuntimeException("create directory failed。");
        }
    }
}
