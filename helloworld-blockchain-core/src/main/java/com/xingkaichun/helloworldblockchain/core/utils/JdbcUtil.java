package com.xingkaichun.helloworldblockchain.core.utils;

/**
 * Jdbc工具
 *
 * 能同时适用于android、window、linux平台
 *
 * android平台用SQLDroid
 * https://github.com/SQLDroid/SQLDroid
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class JdbcUtil {

    private static String jdbcConnectionFormat;

    static {
        try {
            if(OperateSystemUtil.isAndroidOperateSystem()){
                Class.forName("org.sqldroid.SQLDroidDriver");
                jdbcConnectionFormat = "jdbc:sqldroid:%s";
            }else {
                Class.forName("org.sqlite.JDBC");
                jdbcConnectionFormat = "jdbc:sqlite:%s";
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取JDBC ConnectionUrl
     *
     * @author 邢开春 xingkaichun@qq.com
     */
    public static String getJdbcConnectionUrl(String databasePath){
        return String.format(jdbcConnectionFormat,databasePath);
    }
}
