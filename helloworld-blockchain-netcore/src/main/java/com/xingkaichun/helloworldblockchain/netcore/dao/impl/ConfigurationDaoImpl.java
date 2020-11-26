package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JdbcUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.ConfigurationDao;
import com.xingkaichun.helloworldblockchain.netcore.entity.ConfigurationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class ConfigurationDaoImpl implements ConfigurationDao {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationDaoImpl.class);


    public ConfigurationDaoImpl(String blockchainDataPath) {
        this.blockchainDataPath = blockchainDataPath;
        initDatabase();
    }

    private void initDatabase() {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS [Configuration](" +
                "  [confKey] VARCHAR(100)," +
                "  [confValue] VARCHAR(100));";
        JdbcUtil.executeSql(connection(),createTable1Sql1);
    }

    @Override
    public ConfigurationEntity getConfigurationValue(String confKey) {
        String sql = "select confKey,confValue from Configuration where confKey = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,confKey);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String confValue = resultSet.getString("confValue");
                ConfigurationEntity entity = new ConfigurationEntity();
                entity.setConfKey(confKey);
                entity.setConfValue(confValue);
                return entity;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeResultSet(resultSet);
        }
        return null;
    }

    @Override
    public void addConfiguration(ConfigurationEntity configurationEntity) {
        String sql1 = "INSERT INTO Configuration (confKey, confValue) VALUES (?,?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection().prepareStatement(sql1);
            preparedStatement.setString(1,configurationEntity.getConfKey());
            preparedStatement.setString(2,configurationEntity.getConfValue());
            preparedStatement.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
        }
    }

    @Override
    public void updateConfiguration(ConfigurationEntity configurationEntity) {
        String sql1 = "UPDATE Configuration SET confValue = ? where confKey = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection().prepareStatement(sql1);
            preparedStatement.setString(1,configurationEntity.getConfValue());
            preparedStatement.setString(2,configurationEntity.getConfKey());
            preparedStatement.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
        }
    }


    private static final String NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME = "NetCoreDatabase";
    private static final String NODE_SYNCHRONIZE_DATABASE_File_Name = "ConfigurationDaoImpl.db";

    private String blockchainDataPath;
    private Connection connection;

    private synchronized Connection connection() {
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            File nodeSynchronizeDatabaseDirect = new File(blockchainDataPath,NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME);
            FileUtil.mkdir(nodeSynchronizeDatabaseDirect);
            File nodeSynchronizeDatabasePath = new File(nodeSynchronizeDatabaseDirect,NODE_SYNCHRONIZE_DATABASE_File_Name);
            String jdbcConnectionUrl = JdbcUtil.getJdbcConnectionUrl(nodeSynchronizeDatabasePath.getAbsolutePath());
            connection = DriverManager.getConnection(jdbcConnectionUrl);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
