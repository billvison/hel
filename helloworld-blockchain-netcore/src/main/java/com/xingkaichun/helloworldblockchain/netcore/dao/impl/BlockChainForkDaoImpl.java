package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.core.utils.FileUtil;
import com.xingkaichun.helloworldblockchain.core.utils.JdbcUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.BlockChainForkDao;
import com.xingkaichun.helloworldblockchain.netcore.model.BlockchainForkBlockEntity;

import java.io.File;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlockChainForkDaoImpl implements BlockChainForkDao {

    public BlockChainForkDaoImpl(String blockchainDataPath) {
        this.blockchainDataPath = blockchainDataPath;
        init();
    }

    private void init() {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS [BlockchainFork](" +
                "  [blockHeight] INT(20), " +
                "  [blockHash] VARCHAR(100))";
        JdbcUtil.executeSql(connection(),createTable1Sql1);
    }


    @Override
    public List<BlockchainForkBlockEntity> queryAllBlockchainForkBlock() {
        String sql = "select * from BlockchainFork";
        List<BlockchainForkBlockEntity> nodeList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Integer blockHeight = resultSet.getInt("blockHeight");
                String blockHash = resultSet.getString("blockHash");
                BlockchainForkBlockEntity entity = new BlockchainForkBlockEntity();
                entity.setBlockHeight(BigInteger.valueOf(blockHeight));
                entity.setBlockHash(blockHash);
                nodeList.add(entity);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeResultSet(resultSet);
        }
        return nodeList;
    }

    public void removeAll() {
        String sql1 = "delete from BlockchainFork";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection().prepareStatement(sql1);
            preparedStatement.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
        }
    }

    public void add(BlockchainForkBlockEntity entity) {
        String sql1 = "INSERT INTO BlockchainFork (blockHeight, blockHash)" +
                "        VALUES (?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection().prepareStatement(sql1);
            preparedStatement.setInt(1,entity.getBlockHeight().intValue());
            preparedStatement.setString(2,entity.getBlockHash());
            preparedStatement.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
        }
    }


    private static final String NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME = "NetCoreDatabase";
    private static final String NODE_SYNCHRONIZE_DATABASE_File_Name = "BlockChainForkDaoImpl.db";

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

    @Override
    public void updateBlockchainFork(List<BlockchainForkBlockEntity> entityList) {
        Connection connection = null;
        try {
            connection = connection();
            connection.setAutoCommit(false);
            removeAll();
            if(entityList != null){
                for(BlockchainForkBlockEntity entity:entityList){
                    add(entity);
                }
            }
            connection.commit();
        } catch (Exception e){
            try {
                if(connection != null){
                    connection.rollback();
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            throw new RuntimeException(e);
        }
    }
}
