package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.OperateSystemUtil;

/**
 * 资源路径工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ResourcePathTool {

    /**
     * 获取区块链数据存放目录
     */
    public static String getDataRootPath() {
        String dataRootPath = null;
        if(OperateSystemUtil.isWindowsOperateSystem()){
            dataRootPath = "C:\\HelloworldBlockchainData\\";
        }else if(OperateSystemUtil.isLinuxOperateSystem()){
            dataRootPath = "/opt/HelloworldBlockchainData/";
        }else if(OperateSystemUtil.isAndroidOperateSystem()){
            dataRootPath = "/opt/HelloworldBlockchainData/";
        }else {
            throw new RuntimeException("该系统无默认数据存放的根目录。");
        }
        FileUtil.mkdir(dataRootPath);
        return dataRootPath;
    }
}
