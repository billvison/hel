package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.SynchronizerDataBase;
import com.xingkaichun.helloworldblockchain.core.TransactionDataBase;
import com.xingkaichun.helloworldblockchain.core.utils.NodeTransportUtils;
import com.xingkaichun.helloworldblockchain.node.transport.dto.BlockDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SynchronizerDataBaseDefaultImpl extends SynchronizerDataBase {

    private Logger logger = LoggerFactory.getLogger(SynchronizerDataBaseDefaultImpl.class);
//TODO 数值类型用无线大的类型

    private static final String NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME = "NodeSynchronizeDatabase";
    private static final String NODE_SYNCHRONIZE_DATABASE_File_Name = "NodeSynchronize.db";

    private String blockchainDataPath;
    private TransactionDataBase transactionDataBase;
    private Connection connection;

    public SynchronizerDataBaseDefaultImpl(String blockchainDataPath, TransactionDataBase transactionDataBase) throws Exception {
        this.blockchainDataPath = blockchainDataPath;
        this.transactionDataBase = transactionDataBase;
        init();
    }

    private void init() throws SQLException, ClassNotFoundException {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS NODE " +
                "(" +
                "nodeId CHAR(100) PRIMARY KEY NOT NULL," +
                "status CHAR(10)" +
                ")";
        executeSql(createTable1Sql1);

        String createTable1Sql2 = "CREATE TABLE IF NOT EXISTS DATA " +
                "(" +
                "nodeId CHAR(100) NOT NULL," +
                "blockHeight INTEGER NOT NULL," +
                "blockDto TEXT NOT NULL," +
                "insertTime INTEGER NOT NULL" +
                ")";
        executeSql(createTable1Sql2);
    }

    @Override
    public boolean addBlockDTO(String nodeId, BlockDTO blockDTO) throws Exception {

        transactionDataBase.insertBlockDTO(blockDTO);

        String sql = "INSERT INTO DATA (nodeId,blockHeight,blockDto,insertTime) " +
                "VALUES (?,?,?,?);";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,nodeId);
            //TODO 类型转换失真
            preparedStatement.setInt(2,blockDTO.getHeight().intValue());
            preparedStatement.setString(3, NodeTransportUtils.encode(blockDTO));
            preparedStatement.setLong(4,System.currentTimeMillis());
            preparedStatement.executeUpdate();
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
        }
        return true;
    }

    @Override
    public BigInteger getMinBlockHeight(String nodeId) throws Exception {
        String sql = "SELECT min(blockHeight) as minBlockHeight FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,nodeId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int blockHeight = resultSet.getInt("minBlockHeight");
                //TODO 精度
                return BigInteger.valueOf(blockHeight);
            }
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getMaxBlockHeight(String nodeId) throws Exception {
        String sql = "SELECT max(blockHeight) as maxBlockHeight FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,nodeId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int blockHeight = resultSet.getInt("maxBlockHeight");
                return BigInteger.valueOf(blockHeight);
            }
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
        return null;
    }

    @Override
    public BlockDTO getBlockDto(String nodeId, BigInteger blockHeight) throws Exception {
        String selectBlockDataSql = "SELECT * FROM DATA WHERE nodeId = ? and blockHeight=?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(selectBlockDataSql);
            preparedStatement.setString(1,nodeId);
            //TODO 精度
            preparedStatement.setInt(2,blockHeight.intValue());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String stringBlockDto = resultSet.getString("blockDto");
                return NodeTransportUtils.decodeToBlockDTO(stringBlockDto);
            }
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
        return null;
    }


    @Override
    public boolean hasDataTransferFinishFlag(String nodeId) throws Exception {
        String sql = "SELECT * FROM NODE WHERE status = 'FINISH' and nodeId = ? limit 0,1";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,nodeId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return true;
            }
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
        return false;
    }

    @Override
    public String getDataTransferFinishFlagNodeId() throws Exception {
        String sql = "SELECT * FROM NODE WHERE status = 'FINISH' limit 0,1";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String nodeId = resultSet.getString("nodeId");
                return nodeId;
            }
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
        return null;
    }

    @Override
    public String getNodeId() throws Exception {
        String sql = "SELECT * FROM NODE limit 0,1";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String nodeId = resultSet.getString("nodeId");
                return nodeId;
            }
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
        return null;
    }

    @Override
    public List<String> getAllNodeId() throws Exception {
        String sql = "SELECT * FROM NODE";
        List<String> nodeList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String nodeId = resultSet.getString("nodeId");
                nodeList.add(nodeId);
            }
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
        return nodeList;
    }

    @Override
    public long getLastUpdateTimestamp(String nodeId) throws Exception {
        String selectBlockDataSql = "SELECT insertTime FROM DATA WHERE nodeId = ? order by insertTime desc limit 0,1";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(selectBlockDataSql);
            preparedStatement.setString(1,nodeId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getLong("insertTime");
            }
            return 0;
        } finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        }
    }

    @Override
    public void addDataTransferFinishFlag(String nodeId) throws Exception {
        String sql1 = "DELETE FROM NODE WHERE nodeId = ?";
        PreparedStatement preparedStatement1 = null;
        String sql2 = "INSERT INTO NODE (nodeId,status) VALUES (? , ?)";
        PreparedStatement preparedStatement2 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,nodeId);
            preparedStatement1.executeUpdate();
            preparedStatement2 = connection().prepareStatement(sql2);
            preparedStatement2.setString(1,nodeId);
            preparedStatement2.setString(2,"FINISH");
            preparedStatement2.executeUpdate();
        } finally {
            if(preparedStatement1 != null){
                preparedStatement1.close();
            }
            if(preparedStatement2 != null){
                preparedStatement2.close();
            }
        }
    }

    @Override
    public void clear(String nodeId) throws Exception {
        String sql = "DELETE FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement1 = null;
        String sql2 = "DELETE FROM NODE WHERE nodeId = ?";
        PreparedStatement preparedStatement2 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql);
            preparedStatement1.setString(1,nodeId);
            preparedStatement1.executeUpdate();
            preparedStatement2 = connection().prepareStatement(sql2);
            preparedStatement2.setString(1,nodeId);
            preparedStatement2.executeUpdate();
        } finally {
            if(preparedStatement1 != null){
                preparedStatement1.close();
            }
            if(preparedStatement2 != null){
                preparedStatement2.close();
            }
        }
    }

    @Override
    public void clearDB() throws Exception {
        String sql = "DELETE FROM DATA";
        PreparedStatement preparedStatement1 = null;
        String sql2 = "DELETE FROM NODE";
        PreparedStatement preparedStatement2 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql);
            preparedStatement1.executeUpdate();
            preparedStatement2 = connection().prepareStatement(sql2);
            preparedStatement2.executeUpdate();
        } finally {
            if(preparedStatement1 != null){
                preparedStatement1.close();
            }
            if(preparedStatement2 != null){
                preparedStatement2.close();
            }
        }
    }

    private synchronized Connection connection() throws ClassNotFoundException, SQLException {
        if(connection != null && !connection.isClosed()){
            return connection;
        }
        Class.forName("org.sqlite.JDBC");
        File nodeSynchronizeDatabaseDirect = new File(blockchainDataPath,NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME);
        nodeSynchronizeDatabaseDirect.mkdirs();
        File nodeSynchronizeDatabasePath = new File(nodeSynchronizeDatabaseDirect,NODE_SYNCHRONIZE_DATABASE_File_Name);
        String dbUrl = String.format("jdbc:sqlite:%s",nodeSynchronizeDatabasePath.getAbsolutePath());
        connection = DriverManager.getConnection(dbUrl);
        return connection;
    }

    private void executeSql(String sql) throws SQLException, ClassNotFoundException {
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
