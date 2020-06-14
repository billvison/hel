package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.core.utils.SqldroidUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.SimpleNode;
import com.xingkaichun.helloworldblockchain.netcore.model.NodeEntity;

import java.io.File;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NodeDaoImpl implements NodeDao {

    public NodeDaoImpl(String blockchainDataPath) throws Exception {
        this.blockchainDataPath = blockchainDataPath;
        init();
    }

    private void init() throws Exception {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS [Node](" +
                "  [ip] VARCHAR(20) NOT NULL, " +
                "  [port] INTEGER(10) NOT NULL, " +
                "  [blockChainHeight] INTEGER(20) NOT NULL, " +
                "  [isNodeAvailable] INTEGER(10) NOT NULL, " +
                "  [errorConnectionTimes] INTEGER(10) NOT NULL, " +
                "  [fork] INTEGER(10) NOT NULL, " +
                "  UNIQUE([ip], [port]));";
        executeSql(createTable1Sql1);
    }

    @Override
    public NodeEntity queryNode(SimpleNode simpleNode){
        String sql = "select * from Node WHERE ip = ? and port = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,simpleNode.getIp());
            preparedStatement.setInt(2,simpleNode.getPort());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                return resultSetToNodeEntity(resultSet);
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
    String createTable1Sql1 = "CREATE TABLE \"Node\"(" +
            "  [ip] VARCHAR(20) NOT NULL, " +
            "  [port] INTEGER(10) NOT NULL, " +
            "  [blockChainHeight] INTEGER(20) NOT NULL, " +
            "  [isNodeAvailable] INTEGER(10) NOT NULL, " +
            "  [errorConnectionTimes] INTEGER(10) NOT NULL, " +
            "  [fork] INTEGER(10) NOT NULL, " +
            "  UNIQUE([ip], [port]));";


    public static boolean intToBoolean(Integer intValue){
        return Integer.valueOf(0).compareTo(intValue)==0?false:true;
    }
    public static int booleanToInt(Boolean booleanValue){
        if(booleanValue == null || !booleanValue){
            return 0;
        }
        return 1;
    }

    @Override
    public List<NodeEntity> queryAllNoForkNodeList(){
        String sql = "select * from Node where fork = 0";
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
        return list;
    }

    @Override
    public List<NodeEntity> queryAllNoForkAliveNodeList(){
        String sql = "select * from Node WHERE isNodeAvailable = 1 and fork = 0";
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
        return list;
    }

    @Override
    public void addNode(NodeEntity node){
        String sql1 = "        INSERT INTO Node (ip, port, blockChainHeight, isNodeAvailable, errorConnectionTimes, fork)" +
                "        VALUES (?,?,?,?,?,?)";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,node.getIp());
            preparedStatement1.setInt(2,node.getPort());
            preparedStatement1.setInt(3,node.getBlockChainHeight().intValue());
            preparedStatement1.setInt(4,booleanToInt(node.getIsNodeAvailable()));
            preparedStatement1.setInt(5,node.getErrorConnectionTimes());
            preparedStatement1.setInt(6,booleanToInt(node.getFork()));
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
    public void updateNode(NodeEntity node){
        //TODO 赋值
        String sql1 = "UPDATE Node SET blockChainHeight = ? ,isNodeAvailable = ? ," +
                "                errorConnectionTimes = ?, fork = ? where ip = ? and port = ?";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setInt(1,node.getBlockChainHeight().intValue());
            preparedStatement1.setInt(2,booleanToInt(node.getIsNodeAvailable()));
            preparedStatement1.setInt(3,node.getErrorConnectionTimes());
            preparedStatement1.setInt(4,booleanToInt(node.getFork()));
            preparedStatement1.setString(5,node.getIp());
            preparedStatement1.setInt(6,node.getPort());
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
    public boolean deleteNode(SimpleNode simpleNode){
        String sql1 = "delete from Node WHERE ip = ? and port = ?";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,simpleNode.getIp());
            preparedStatement1.setInt(2,simpleNode.getPort());
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
        return true;
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
        return list;
    }



    private static final String NODE_SYNCHRONIZE_DATABASE_DIRECT_NAME = "NetCoreDatabase";
    private static final String NODE_SYNCHRONIZE_DATABASE_File_Name = "NodeDaoImpl.db";

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



    private NodeEntity resultSetToNodeEntity(ResultSet resultSet) throws Exception {
        String ip = resultSet.getString("ip");
        Integer port = resultSet.getInt("port");
        Integer blockChainHeight = resultSet.getInt("blockChainHeight");
        Integer isNodeAvailable = resultSet.getInt("isNodeAvailable");
        Integer errorConnectionTimes = resultSet.getInt("errorConnectionTimes");
        Integer fork = resultSet.getInt("fork");

        NodeEntity entity = new NodeEntity();
        entity.setIp(ip);
        entity.setPort(port);
        entity.setBlockChainHeight(BigInteger.valueOf(blockChainHeight));
        entity.setIsNodeAvailable(intToBoolean(isNodeAvailable));
        entity.setErrorConnectionTimes(errorConnectionTimes);
        entity.setFork(intToBoolean(fork));
        return entity;
    }
}
