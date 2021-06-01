package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.po.NodePo;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class NodeDaoImpl implements NodeDao {

    private static final String NODE_DATABASE_NAME = "NodeDatabase";
    private String nodeDatabasePath ;

    public NodeDaoImpl(NetCoreConfiguration netCoreConfiguration) {
        this.nodeDatabasePath = FileUtil.newPath(netCoreConfiguration.getNetCorePath(), NODE_DATABASE_NAME);
    }

    @Override
    public NodePo queryNode(String ip){
        List<NodePo> nodePoList = queryAllNodeList();
        if(nodePoList != null){
            for(NodePo n: nodePoList){
                if(StringUtil.isEquals(ip,n.getIp())){
                    return n;
                }
            }
        }
        return null;
    }

    @Override
    public void addNode(NodePo node){
        KvDbUtil.put(nodeDatabasePath,ByteUtil.stringToUtf8Bytes(node.getIp()), ByteUtil.stringToUtf8Bytes(JsonUtil.toJson(node)));
    }

    @Override
    public void updateNode(NodePo node){
        KvDbUtil.put(nodeDatabasePath,ByteUtil.stringToUtf8Bytes(node.getIp()),ByteUtil.stringToUtf8Bytes(JsonUtil.toJson(node)));
    }

    @Override
    public void deleteNode(String ip){
        KvDbUtil.delete(nodeDatabasePath,ByteUtil.stringToUtf8Bytes(ip));
    }

    @Override
    public List<NodePo> queryAllNodeList(){
        List<NodePo> list = new ArrayList<>();
        //获取所有
        List<byte[]> bytesNodeEntityList = KvDbUtil.get(nodeDatabasePath,1,100000000);
        if(bytesNodeEntityList != null){
            for(byte[] bytesNodeEntity:bytesNodeEntityList){
                NodePo nodePo = JsonUtil.fromJson(ByteUtil.utf8BytesToString(bytesNodeEntity), NodePo.class);
                list.add(nodePo);
            }
        }
        return list;
    }
}
