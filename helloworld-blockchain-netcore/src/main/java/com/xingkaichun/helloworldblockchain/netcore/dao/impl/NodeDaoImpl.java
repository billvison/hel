package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JdbcUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeDaoImpl implements NodeDao {

    public NodeDaoImpl(String blockchainDataPath) {
        this.blockchainDataPath = blockchainDataPath;
        initDatabase();
    }

    private void initDatabase() {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS [Node](" +
                "  [ip] VARCHAR(20) PRIMARY KEY NOT NULL, " +
                "  [blockchainHeight] INTEGER NOT NULL" +
                ");";
        JdbcUtil.executeSql(connection(),createTable1Sql1);
    }

    @Override
    public NodeEntity queryNode(String ip){
        String sql = "select * from Node WHERE ip = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,ip);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSetToNodeEntity(resultSet);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeResultSet(resultSet);
        }
        return null;
    }

    @Override
    public void addNode(NodeEntity node){
        String sql1 = "INSERT INTO Node (ip, blockchainHeight)" +
                "        VALUES (?,?)";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,node.getIp());
            preparedStatement1.setLong(2,node.getBlockchainHeight().intValue());
            preparedStatement1.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement1);
        }
    }

    @Override
    public void updateNode(NodeEntity node){
        NodeEntity nodeEntity = queryNode(node.getIp());
        if(node.getBlockchainHeight()==null){
            node.setBlockchainHeight(nodeEntity.getBlockchainHeight());
        }

        String sql1 = "UPDATE Node SET blockchainHeight = ? where ip = ?";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setLong(1,node.getBlockchainHeight().intValue());
            preparedStatement1.setString(2,node.getIp());
            preparedStatement1.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement1);
        }
    }

    @Override
    public void deleteNode(String ip){
        String sql1 = "delete from Node WHERE ip = ?";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,ip);
            preparedStatement1.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement1);
        }
    }

    @Override
    public List<NodeEntity> queryAllNodeList(){
        String sql = "select * from Node";
        PreparedStatement preparedStatement = null;
        List<NodeEntity> list = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                list.add(resultSetToNodeEntity(resultSet));
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeResultSet(resultSet);
        }
        return list;
    }



    private static final String NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME = "NetCoreDatabase";
    private static final String NODE_SYNCHRONIZE_DATABASE_File_Name = "NodeDaoImpl.db";

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

    private NodeEntity resultSetToNodeEntity(ResultSet resultSet) {
        try {
            String ip = resultSet.getString("ip");
            long blockchainHeight = resultSet.getLong("blockchainHeight");

            NodeEntity entity = new NodeEntity();
            entity.setIp(ip);
            entity.setBlockchainHeight(blockchainHeight);
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
