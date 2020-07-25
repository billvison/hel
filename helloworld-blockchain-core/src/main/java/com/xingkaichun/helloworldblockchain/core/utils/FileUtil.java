package com.xingkaichun.helloworldblockchain.core.utils;

import java.io.File;

/**
 * File工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class FileUtil {

    public static void mkdir(String path) {
        mkdir(new File(path));
    }

    public static void mkdir(File file) {
        if(file.exists()){
            return;
        }
        boolean mkdirs = file.mkdirs();
        if(!mkdirs){
            throw new RuntimeException("创建目录失败。");
        }
    }
}
