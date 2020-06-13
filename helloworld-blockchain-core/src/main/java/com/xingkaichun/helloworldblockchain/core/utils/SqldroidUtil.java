package com.xingkaichun.helloworldblockchain.core.utils;

/**
 * SQLDroid工具
 * https://github.com/SQLDroid/SQLDroid
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SqldroidUtil {

    public static Boolean isAndroid;
    public static String jdbcConnectionFormat;
    static {
        try {
            Class.forName("android.app.Application");
            isAndroid = true;
        } catch (ClassNotFoundException e) {
            isAndroid = false;
        }
        try {
            if(isAndroid){
                Class.forName("org.sqldroid.SQLDroidDriver");
                jdbcConnectionFormat = "jdbc:sqldroid:%s";
            }else {
                Class.forName("org.sqlite.JDBC");
                jdbcConnectionFormat = "jdbc:sqlite:%s";
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
