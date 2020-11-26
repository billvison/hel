package com.xingkaichun.helloworldblockchain.util;

import java.io.File;

/**
 * File工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
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
