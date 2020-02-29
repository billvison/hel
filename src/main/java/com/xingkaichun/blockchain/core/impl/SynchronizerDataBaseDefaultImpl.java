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

    private String dbPath;
    private String dbFileName;
    private TransactionDataBase transactionDataBase;
    private Connection connection;

    public SynchronizerDataBaseDefaultImpl(String dbPath, String dbFileName, TransactionDataBase transactionDataBase) throws Exception {
        this.dbPath = dbPath;
        this.dbFileName = dbFileName;
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
                "blockDto BLOB" +
                ")";
        executeSql(createTable1Sql2);
    }

    @Override
    public boolean addBlockDTO(String nodeId, BlockDTO blockDTO) throws Exception {

        transactionDataBase.insertBlockDTO(blockDTO);

        String sql = "INSERT INTO DATA (nodeId,blockHeight,blockDto) " +
                "VALUES (?, ?, ?);";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        preparedStatement.setInt(2,blockDTO.getHeight());
        preparedStatement.setBlob(3,new SerialBlob(DtoUtils.encode(blockDTO)));
        preparedStatement.executeUpdate();
        return true;
    }

    @Override
    public int getMinBlockHeight(String nodeId) throws Exception {
        String sql = "SELECT min(blockHeight) FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int blockHeight = resultSet.getInt("blockHeight");
            return blockHeight;
        }
        return -1;
    }

    @Override
    public int getMaxBlockHeight(String nodeId) throws Exception {
        String sql = "SELECT max(blockHeight) FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int blockHeight = resultSet.getInt("blockHeight");
            return blockHeight;
        }
        return -1;
    }

    @Override
    public BlockDTO getBlockDto(String nodeId, int blockHeight) throws Exception {
        String selectBlockDataSql = "SELECT * FROM DATA WHERE nodeId = ? and blockHeight=?";
        PreparedStatement preparedStatement = connection().prepareStatement(selectBlockDataSql);
        preparedStatement.setString(1,nodeId);
        preparedStatement.setInt(2,blockHeight);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            Blob blob = resultSet.getBlob("blockDto");
            byte[] byteBlockDto = blob.getBytes(0, (int) blob.length());
            return DtoUtils.decodeToBlockDTO(byteBlockDto);
        }
        return null;
    }


    @Override
    public boolean hasDataTransferFinishFlag(String nodeId) throws Exception {
        String sql = "SELECT top 1 * FROM NODE WHERE status = 'FINISH' and nodeId = ?";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            return true;
        }
        return false;
    }

    @Override
    public String getDataTransferFinishFlagNodeId() throws Exception {
        String sql = "SELECT * FROM NODE WHERE status = 'FINISH' limit 0,1";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String nodeId = resultSet.getString("nodeId");
            return nodeId;
        }
        return null;
    }

    @Override
    public String getNodeId() throws Exception {
        String sql = "SELECT * FROM NODE limit 0,1";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String nodeId = resultSet.getString("nodeId");
            return nodeId;
        }
        return null;
    }

    @Override
    public void addDataTransferFinishFlag(String nodeId) throws Exception {
        String sql1 = "DELETE NODE WHERE nodeId = ?";
        PreparedStatement preparedStatement1 = connection().prepareStatement(sql1);
        preparedStatement1.setString(1,nodeId);
        preparedStatement1.executeUpdate();

        String sql2 = "INSERT NODE (nodeId,status) VALUES (? , ?)";
        PreparedStatement preparedStatement2 = connection().prepareStatement(sql2);
        preparedStatement2.setString(1,nodeId);
        preparedStatement2.setString(2,"FINISH");
        preparedStatement2.executeUpdate();
    }

    @Override
    public void clear(String nodeId) throws Exception {
        String sql = "DELETE FROM DATA WHERE nodeId = ?";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.setString(1,nodeId);
        preparedStatement.executeUpdate();

        String sql2 = "DELETE FROM NODE WHERE nodeId = ?";
        PreparedStatement preparedStatement2 = connection().prepareStatement(sql2);
        preparedStatement2.setString(1,nodeId);
        preparedStatement2.executeUpdate();
    }

    @Override
    public void clearDB() throws Exception {
        String sql = "DELETE FROM DATA";
        PreparedStatement preparedStatement = connection().prepareStatement(sql);
        preparedStatement.executeUpdate();

        String sql2 = "DELETE FROM NODE";
        PreparedStatement preparedStatement2 = connection().prepareStatement(sql2);
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

    private void executeSql(String sql) throws SQLException, ClassNotFoundException {
        Statement stmt = connection().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }
}
