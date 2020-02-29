package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.SynchronizerDataBase;
import com.xingkaichun.blockchain.core.TransactionDataBase;
import com.xingkaichun.blockchain.core.dto.BlockDTO;
import com.xingkaichun.blockchain.core.dto.DtoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.sql.*;

public class SynchronizerDataBaseDefaultImpl extends SynchronizerDataBase {

    private Logger logger = LoggerFactory.getLogger(SynchronizerDataBaseDefaultImpl.class);

    String dbPath;
    String dbFileName;
    private TransactionDataBase transactionDataBase;

    private Connection connection;
    public SynchronizerDataBaseDefaultImpl(String dbPath, String dbFileName, TransactionDataBase transactionDataBase) throws Exception {
        this.dbPath = dbPath;
        this.dbFileName = dbFileName;
        this.transactionDataBase = transactionDataBase;

        init();
    }

    @Override
    public boolean addBlockDTO(String nodeId, BlockDTO blockDTO) throws Exception {

        transactionDataBase.insertBlockDTO(blockDTO);

        String sql = "INSERT INTO DATA (nodeId,blockHeight,block) " +
                "VALUES (?, ?, ?);";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        preparedStatement.setInt(2,blockDTO.getHeight());
        preparedStatement.setBlob(3,new SerialBlob(DtoUtils.encode(blockDTO)));
        preparedStatement.executeUpdate();
        return true;
    }

    @Override
    public BlockDTO getNextBlockDTO(String nodeId) throws Exception {
        int intNextBlockHeight = 0;
        String sql1 = "SELECT nextBlockHeight FROM NODE WHERE status = 'FINISH' nodeId = ?";
        PreparedStatement preparedStatement1 = connection().prepareStatement(sql1);
        ResultSet resultSet = preparedStatement1.executeQuery();
        while (resultSet.next()){
            Object objNextBlockHeight = resultSet.getObject("nextBlockHeight");
            if(objNextBlockHeight == null){
                String minBlockHeightSql = "SELECT min(blockHeight) FROM DATA WHERE nodeId = ?";
                PreparedStatement minBlockHeightPreparedStatement = connection().prepareStatement(minBlockHeightSql);
                ResultSet minBlockHeightResultSet = minBlockHeightPreparedStatement.executeQuery();
                if (resultSet.next()){
                    intNextBlockHeight = resultSet.getInt("blockHeight");
                } else {
                    return null;
                }
            } else {
                intNextBlockHeight = Integer.valueOf(objNextBlockHeight.toString());
            }
            //更新next
            String setNextSql = "UPDATE NODE SET nextBlockHeight = ? WHERE nodeId = ?";
            PreparedStatement setNextPreparedStatement = connection().prepareStatement(setNextSql);
            setNextPreparedStatement.setInt(1,1+intNextBlockHeight);
            setNextPreparedStatement.setString(2,nodeId);
        }
        return null;
    }

    @Override
    public int getMaxBlockHeight(String nodeId) throws Exception {
        String sql1 = "SELECT max(blockHeight) FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement1 = connection().prepareStatement(sql1);
        ResultSet resultSet = preparedStatement1.executeQuery();
        while (resultSet.next()){
            int blockHeight = resultSet.getInt("blockHeight");
            return blockHeight;
        }
        return -1;
    }

    @Override
    public boolean hasDataTransferFinishFlag(String nodeId) throws Exception {
        String sql1 = "SELECT top 1 * FROM NODE WHERE status = 'FINISH' and nodeId = ?";
        PreparedStatement preparedStatement1 = connection().prepareStatement(sql1);
        preparedStatement1.setString(1,nodeId);
        ResultSet resultSet = preparedStatement1.executeQuery();
        while (resultSet.next()){
            return true;
        }
        return false;
    }

    @Override
    public String getDataTransferFinishFlagNodeId() throws Exception {
        String sql1 = "SELECT * FROM NODE WHERE status = 'FINISH' limit 0,1";
        PreparedStatement preparedStatement2 = connection().prepareStatement(sql1);
        ResultSet resultSet = preparedStatement2.executeQuery();
        while (resultSet.next()){
            String nodeId = resultSet.getString("nodeId");
            return nodeId;
        }
        return null;
    }

    @Override
    public void addDataTransferFinishFlag(String nodeId) throws Exception {
        String sql1 = "INSERT NODE (nodeId,status) VALUES (? , ?)";
        PreparedStatement preparedStatement2 = connection().prepareStatement(sql1);
        preparedStatement2.setString(1,nodeId);
        preparedStatement2.setString(2,"FINISH");
        preparedStatement2.executeUpdate();
    }

    @Override
    public void clear(String nodeId) throws Exception {
        String sql = "DELETE DATA WHERE  nodeId = ?";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        preparedStatement.executeUpdate();

        String sql2 = "DELETE NODE WHERE nodeId = ?";
        PreparedStatement preparedStatement2 = connection().prepareStatement(sql2);
        preparedStatement2.setString(1,nodeId);
        preparedStatement2.executeUpdate();
    }

    private synchronized Connection connection() throws ClassNotFoundException, SQLException {
        if(connection != null && !connection.isClosed()){
            return connection;
        }
        Class.forName("org.sqlite.JDBC");
        File dataPath = new File(dbPath);
        dataPath.mkdirs();
        File dataFile = new File(dataPath,dbFileName);
        connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile.getAbsolutePath());
        return connection;
    }

    private void init() throws SQLException, ClassNotFoundException {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS NODE " +
                "(" +
                "nodeId CHAR(100) PRIMARY KEY NOT NULL," +
                "status CHAR(10)," +
                "nextBlockHeight INTEGER" +
                ")";
        executeSql(createTable1Sql1);

        String createTable1Sql2 = "CREATE TABLE IF NOT EXISTS DATA " +
                "(" +
                "nodeId CHAR(100) NOT NULL," +
                "blockHeight INTEGER NOT NULL," +
                "block BLOB" +
                ")";
        executeSql(createTable1Sql2);
    }

    private void executeSql(String sql) throws SQLException, ClassNotFoundException {
        Statement stmt = connection().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }
}
