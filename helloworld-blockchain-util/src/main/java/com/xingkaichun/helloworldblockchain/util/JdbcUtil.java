package com.xingkaichun.helloworldblockchain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Jdbc工具
 *
 * @author 邢开春 409060350@qq.com
 */
public class JdbcUtil {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    private static String JDBC_CONNECTION_FORMAT = "jdbc:sqlite:%s";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getConnectionUrl(String databasePath){
        return String.format(JDBC_CONNECTION_FORMAT,databasePath);
    }

    public static void closeStatement(Statement stmt) {
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.debug("close statement failed.",e);
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.debug("close resultSet failed.",e);
            }
        }
    }

    public static void executeSql(Connection connection, String sql) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeStatement(stmt);
        }
    }
}
