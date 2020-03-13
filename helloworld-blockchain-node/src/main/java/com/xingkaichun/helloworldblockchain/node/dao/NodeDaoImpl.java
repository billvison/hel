package com.xingkaichun.helloworldblockchain.node.dao;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class NodeDaoImpl implements NodeDao {

    private static final Logger logger = LoggerFactory.getLogger(NodeDaoImpl.class);

    @Value("${blockchainDataPath}")
    protected String blockchainDataPath;
    @Value("${nodeserver.seedNodes}")
    private String seedNodes;

    private static final String NODE_INFO_DATABASE_DIRECT_NAME = "NodeInfoDatabase";
    private static final String NODE_INFO_DATABASE_File_Name = "nodeInfo.db";

    private Connection connection;



    @PostConstruct
    private void initDatabase() {
        String createTable1Sql = "CREATE TABLE IF NOT EXISTS NODE " +
                "(" +
                "nodeId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ip CHAR(20)," +
                "port INTEGER," +
                "blockChainHeight INTEGER," +
                "isNodeAvailable INTEGER," +
                "errorConnectionTimes INTEGER" +
                ")";
        executeSql(createTable1Sql);

        //插入种子节点
        if(seedNodes == null || "".equals(seedNodes)){
            logger.error("没有设置种子节点");
        } else {
            String[] ipPortArray = seedNodes.split(",");
            for(String ipPort:ipPortArray){
                String ip = ipPort.split(":")[0];
                String port = ipPort.split(":")[1];
                Node dbNode = queryNode(ip,Integer.valueOf(port));
                if(dbNode != null){
                    continue;
                }
                Node node = new Node();
                node.setIp(ip);
                node.setPort(Integer.valueOf(port));
                node.setBlockChainHeight(0);
                node.setNodeAvailable(true);
                node.setErrorConnectionTimes(0);
                addNode(node);
            }
        }
    }

    @Override
    public Node queryNode(String ip,int port) {
        try {
            String updateNodeSql = "select * from NODE " +
                    "WHERE ip = ? and port = ?";
            PreparedStatement preparedStatement = connection().prepareStatement(updateNodeSql);
            preparedStatement.setString(1,ip);
            preparedStatement.setInt(2,port);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Node> nodeList = convert(resultSet);
            if(nodeList == null || nodeList.size()==0){
                return null;
            } else {
                return nodeList.get(0);
            }
        } catch (SQLException e) {
            logger.error("sql异常！",e);
            return null;
        }
    }

    @Override
    public List<Node> queryAllNodeList() {
        String sql = "select * from NODE" ;
        return executeQuery(sql);
    }

    @Override
    public List<Node> queryAliveNodes() {
        String queryAliveNodesSql = "select * from NODE " +
                "WHERE isNodeAvailable = 1";
        return executeQuery(queryAliveNodesSql);
    }

    @Override
    public void addNode(Node node) {
        try {
            String addNodeSql = "INSERT INTO NODE (ip,port,blockChainHeight,isNodeAvailable,errorConnectionTimes) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection().prepareStatement(addNodeSql);
            preparedStatement.setString(1,node.getIp());
            preparedStatement.setInt(2,node.getPort());
            preparedStatement.setInt(3,node.getBlockChainHeight());
            preparedStatement.setInt(4,node.isNodeAvailable()?1:0);
            preparedStatement.setInt(5,node.getErrorConnectionTimes());
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("sql异常！",e);
        }
    }

    @Override
    public int updateNode(Node node) {
        try {
            String updateNodeSql = "UPDATE NODE SET blockChainHeight = ?,isNodeAvailable = ?,errorConnectionTimes = ? " +
                    "WHERE ip = ? and port = ?";
            PreparedStatement preparedStatement = connection().prepareStatement(updateNodeSql);
            preparedStatement.setInt(1,node.getBlockChainHeight());
            preparedStatement.setInt(2,node.isNodeAvailable()?1:0);
            preparedStatement.setInt(3,node.getErrorConnectionTimes());
            preparedStatement.setString(4,node.getIp());
            preparedStatement.setInt(5,node.getPort());
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("sql异常！",e);
            return 0;
        }
    }

    @Override
    public boolean deleteNode(String ip, int port) {
        try {
            String deleteSql = "delete from NODE WHERE ip = ? and port = ?" ;
            PreparedStatement preparedStatement = connection().prepareStatement(deleteSql);
            preparedStatement.setString(1,ip);
            preparedStatement.setInt(2,port);
            return preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("sql异常！",e);
            return false;
        }
    }

    private synchronized Connection connection() {
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            File nodeInfoDatabaseDirect = new File(blockchainDataPath,NODE_INFO_DATABASE_DIRECT_NAME);
            nodeInfoDatabaseDirect.mkdirs();
            File nodeInfoDatabasePath = new File(nodeInfoDatabaseDirect,NODE_INFO_DATABASE_File_Name);
            String dbUrl = String.format("jdbc:sqlite:%s",nodeInfoDatabasePath.getAbsolutePath());
            connection = DriverManager.getConnection(dbUrl);
            return connection;
        } catch (SQLException e) {
            logger.error("sql异常！",e);
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            logger.error("class异常！",classNotFoundException);
            return null;
        }
    }

    private void executeSql(String sql) {
        try {
            Statement stmt = connection().createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            logger.error("sql异常！",e);
        }
    }

    private List<Node> executeQuery(String sql) {
        try {
            Statement stmt = connection().createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            List<Node> nodeList = convert(resultSet);
            stmt.close();
            return nodeList;
        } catch (SQLException e) {
            logger.error("sql异常！",e);
            return null;
        }
    }

    public List<Node> convert(ResultSet resultSet) {
        try {
            List<Node> nodeList = new ArrayList<>();
            while (resultSet.next()){
                Node node = new Node();
                node.setIp(resultSet.getString("ip"));
                node.setPort(resultSet.getInt("port"));
                node.setBlockChainHeight(resultSet.getInt("blockChainHeight"));
                node.setNodeAvailable(resultSet.getInt("isNodeAvailable")==1);
                node.setErrorConnectionTimes(resultSet.getInt("errorConnectionTimes"));
                nodeList.add(node);
            }
            return nodeList;
        } catch (SQLException e) {
            logger.error("sql异常！",e);
            return null;
        }
    }
}
