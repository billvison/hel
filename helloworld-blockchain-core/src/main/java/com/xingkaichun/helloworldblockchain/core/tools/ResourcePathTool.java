package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.OperateSystemUtil;

import java.io.File;

/**
 * 资源路径工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ResourcePathTool {

    /**
     * 获取区块链数据存放目录
     */
    public static String getDataRootPath() {
        return getDataRootPath(null);
    }
    /**
     * 获取区块链数据存放目录
     */
    public static String getDataRootPath(String defaultDataRootPath) {
        String dataRootPath = defaultDataRootPath;
        if(Strings.isNullOrEmpty(dataRootPath)){
            if(OperateSystemUtil.isWindowsOperateSystem()){
                dataRootPath = "C:\\HelloworldBlockchainData\\";
            }else if(OperateSystemUtil.isLinuxOperateSystem()){
                dataRootPath = "/opt/HelloworldBlockchainData/";
            }
        }
        if(Strings.isNullOrEmpty(dataRootPath)){
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            dataRootPath = new File(path,"HelloworldBlockchainData").getAbsolutePath();
        }
        FileUtil.mkdir(dataRootPath);
        return dataRootPath;
    }
}
