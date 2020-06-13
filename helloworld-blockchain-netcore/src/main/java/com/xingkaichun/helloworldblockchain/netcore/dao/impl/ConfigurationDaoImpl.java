package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.core.utils.SqldroidUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.model.ConfigurationEntity;

import java.io.File;
import java.sql.*;

public class ConfigurationDaoImpl implements ConfigurationDao {


    public ConfigurationDaoImpl(String blockchainDataPath) throws Exception {
        this.blockchainDataPath = blockchainDataPath;
        init();
    }

    private void init() throws Exception {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS [Configuration](" +
                "  [confKey] VARCHAR(100)," +
                "  [confValue] VARCHAR(100));";
        executeSql(createTable1Sql1);
    }

    @Override
    public String getConfiguratioValue(String confKey) {
        String sql = "select confValue from Configuration where confKey = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,confKey);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String confValue = resultSet.getString("confValue");
                return confValue;
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                }
            }
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
        }
        return null;
    }

    @Override
    public void addConfiguration(ConfigurationEntity configurationEntity) {
        String sql1 = "INSERT INTO Configuration (confKey, confValue) VALUES ?,?)";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,configurationEntity.getConfKey());
            preparedStatement1.setString(2,configurationEntity.getConfValue());
            preparedStatement1.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if(preparedStatement1 != null){
                try {
                    preparedStatement1.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public void updateConfiguration(ConfigurationEntity configurationEntity) {
        String sql1 = "UPDATE Configuration SET confValue = ? where confKey = ?";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,configurationEntity.getConfValue());
            preparedStatement1.setString(2,configurationEntity.getConfKey());
            preparedStatement1.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if(preparedStatement1 != null){
                try {
                    preparedStatement1.close();
                } catch (SQLException e) {
                }
            }
        }
    }


    private static final String NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME = "NetCoreDatabase";
    private static final String NODE_SYNCHRONIZE_DATABASE_File_Name = "ConfigurationDaoImpl.db";

    private String blockchainDataPath;
    private Connection connection;

    private synchronized Connection connection() throws Exception {
        if(connection != null && !connection.isClosed()){
            return connection;
        }
        File nodeSynchronizeDatabaseDirect = new File(blockchainDataPath,NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME);
        nodeSynchronizeDatabaseDirect.mkdirs();
        File nodeSynchronizeDatabasePath = new File(nodeSynchronizeDatabaseDirect,NODE_SYNCHRONIZE_DATABASE_File_Name);
        String jdbcConnectionUrl = SqldroidUtil.getJdbcConnectionUrl(nodeSynchronizeDatabasePath.getAbsolutePath());
        connection = DriverManager.getConnection(jdbcConnectionUrl);
        return connection;
    }
    private void executeSql(String sql) throws Exception {
        Statement stmt = null;
        try {
            stmt = connection().createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } finally {
            if(stmt != null){
                stmt.close();
            }
        }
    }
}
