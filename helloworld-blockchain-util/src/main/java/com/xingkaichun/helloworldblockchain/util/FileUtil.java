package com.xingkaichun.helloworldblockchain.util;

import java.io.File;

/**
 * File工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class FileUtil {

    public static String newPath(String parent, String child) {
        return new File(parent,child).getAbsolutePath();
    }

    public static void mkdirs(String path) {
        File file = new File(path);
        if(file.exists()){
            return;
        }
        boolean mkdirs = file.mkdirs();
        if(!mkdirs){
            throw new RuntimeException("create directory failed.");
        }
    }
}
