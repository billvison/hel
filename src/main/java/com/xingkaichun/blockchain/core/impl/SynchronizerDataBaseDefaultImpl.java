package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.SynchronizerDataBase;
import com.xingkaichun.blockchain.core.TransactionDataBase;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.utils.atomic.EncodeDecode;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;

public class SynchronizerDataBaseDefaultImpl implements SynchronizerDataBase {

    String dbPath;
    private TransactionDataBase transactionDataBase;

    public SynchronizerDataBaseDefaultImpl(String dbPath, TransactionDataBase transactionDataBase) throws Exception {
        this.dbPath = dbPath;
        this.transactionDataBase = transactionDataBase;

        init();
    }

    @Override
    public boolean addBlock(String nodeId, Block block) throws Exception {

        transactionDataBase.insertBlock(block);

        String sql = "INSERT INTO DATA (nodeId,blockHeight,block) " +
                "VALUES (?, ?, ?);";
        PreparedStatement preparedStatement = connection(dbPath).prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        preparedStatement.setInt(2,block.getHeight());
        preparedStatement.setBlob(3,new SerialBlob(EncodeDecode.encode(block)));
        preparedStatement.executeUpdate();

        return true;
    }

    @Override
    public Block getNextBlock(String nodeId) throws Exception {
        int intNextBlockHeight = 0;
        String sql1 = "SELECT nextBlockHeight FROM NODE WHERE status = 'FINISH' nodeId = ?";
        PreparedStatement preparedStatement1 = connection(dbPath).prepareStatement(sql1);
        ResultSet resultSet = preparedStatement1.executeQuery();
        while (resultSet.next()){
            Object objNextBlockHeight = resultSet.getObject("nextBlockHeight");
            if(objNextBlockHeight == null){
                String minBlockHeightSql = "SELECT min(blockHeight) FROM DATA WHERE nodeId = ?";
                PreparedStatement minBlockHeightPreparedStatement = connection(dbPath).prepareStatement(minBlockHeightSql);
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
            PreparedStatement setNextPreparedStatement = connection(dbPath).prepareStatement(setNextSql);
            setNextPreparedStatement.setInt(1,1+intNextBlockHeight);
            setNextPreparedStatement.setString(2,nodeId);
        }
        return null;
    }

    @Override
    public int getMaxBlockHeight(String nodeId) throws Exception {
        String sql1 = "SELECT max(blockHeight) FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement1 = connection(dbPath).prepareStatement(sql1);
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
        PreparedStatement preparedStatement1 = connection(dbPath).prepareStatement(sql1);
        preparedStatement1.setString(1,nodeId);
        ResultSet resultSet = preparedStatement1.executeQuery();
        while (resultSet.next()){
            return true;
        }
        return false;
    }

    @Override
    public String getDataTransferFinishFlagNodeId() throws Exception {
        String sql1 = "SELECT top 1 * FROM NODE WHERE status = 'FINISH'";
        PreparedStatement preparedStatement2 = connection(dbPath).prepareStatement(sql1);
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
        PreparedStatement preparedStatement2 = connection(dbPath).prepareStatement(sql1);
        preparedStatement2.setString(1,nodeId);
        preparedStatement2.setString(2,"FINISH");
        preparedStatement2.executeUpdate();
    }

    @Override
    public void clear(String nodeId) throws Exception {
        String sql = "DELETE DATA WHERE  nodeId = ?";
        PreparedStatement preparedStatement = connection(dbPath).prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        preparedStatement.executeUpdate();

        String sql2 = "DELETE NODE WHERE nodeId = ?";
        PreparedStatement preparedStatement2 = connection(dbPath).prepareStatement(sql2);
        preparedStatement2.setString(1,nodeId);
        preparedStatement2.executeUpdate();
    }

    private Connection connection(String dbPath) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
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
        Statement stmt = connection(dbPath).createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }
}
