package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.core.utils.FileUtil;
import com.xingkaichun.helloworldblockchain.core.utils.JdbcUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.BlockChainBranchDao;
import com.xingkaichun.helloworldblockchain.netcore.model.BlockchainBranchBlockEntity;

import java.io.File;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlockChainBranchDaoImpl implements BlockChainBranchDao {

    public BlockChainBranchDaoImpl(String blockchainDataPath) throws Exception {
        this.blockchainDataPath = blockchainDataPath;
        init();
    }

    private void init() throws Exception {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS [BlockchainBranch](" +
                "  [blockHeight] INT(20), " +
                "  [blockHash] VARCHAR(100))";
        executeSql(createTable1Sql1);
    }


    @Override
    public List<BlockchainBranchBlockEntity> queryAllBlockchainBranchBlock() {
        String sql = "select * from BlockchainBranch";
        List<BlockchainBranchBlockEntity> nodeList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Integer blockHeight = resultSet.getInt("blockHeight");
                String blockHash = resultSet.getString("blockHash");
                BlockchainBranchBlockEntity entity = new BlockchainBranchBlockEntity();
                entity.setBlockHeight(BigInteger.valueOf(blockHeight));
                entity.setBlockHash(blockHash);
                nodeList.add(entity);
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
        return nodeList;
    }

    public void removeAll() {
        String sql1 = "delete from BlockchainBranch";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
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

    public void add(BlockchainBranchBlockEntity entity) {
        String sql1 = "INSERT INTO BlockchainBranch (blockHeight, blockHash)" +
                "        VALUES (?, ?)";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setInt(1,entity.getBlockHeight().intValue());
            preparedStatement1.setString(2,entity.getBlockHash());
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
    private static final String NODE_SYNCHRONIZE_DATABASE_File_Name = "BlockChainBranchDaoImpl.db";

    private String blockchainDataPath;
    private Connection connection;

    private synchronized Connection connection() throws Exception {
        if(connection != null && !connection.isClosed()){
            return connection;
        }
        File nodeSynchronizeDatabaseDirect = new File(blockchainDataPath,NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME);
        FileUtil.mkdir(nodeSynchronizeDatabaseDirect);
        File nodeSynchronizeDatabasePath = new File(nodeSynchronizeDatabaseDirect,NODE_SYNCHRONIZE_DATABASE_File_Name);
        String jdbcConnectionUrl = JdbcUtil.getJdbcConnectionUrl(nodeSynchronizeDatabasePath.getAbsolutePath());
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

    @Override
    public void updateBranchchainBranch(List<BlockchainBranchBlockEntity> entityList) {
        Connection connection = null;
        try {
            connection = connection();
            connection.setAutoCommit(false);
            removeAll();
            for(BlockchainBranchBlockEntity entity:entityList){
                add(entity);
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
        } finally {
        }
    }
}
