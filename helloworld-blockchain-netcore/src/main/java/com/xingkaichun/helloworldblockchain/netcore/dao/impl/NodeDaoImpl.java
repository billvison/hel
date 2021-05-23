package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.po.NodePO;
import com.xingkaichun.helloworldblockchain.netcore.service.NetCoreConfiguration;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.KvDBUtil;
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
    public NodePO queryNode(String ip){
        List<NodePO> nodePOList = queryAllNodeList();
        if(nodePOList != null){
            for(NodePO n: nodePOList){
                if(StringUtil.isEquals(ip,n.getIp())){
                    return n;
                }
            }
        }
        return null;
    }

    @Override
    public void addNode(NodePO node){
        KvDBUtil.put(nodeDatabasePath,ByteUtil.encode(node.getIp()), ByteUtil.encode(JsonUtil.toJson(node)));
    }

    @Override
    public void updateNode(NodePO node){
        KvDBUtil.put(nodeDatabasePath,ByteUtil.encode(node.getIp()),ByteUtil.encode(JsonUtil.toJson(node)));
    }

    @Override
    public void deleteNode(String ip){
        KvDBUtil.delete(nodeDatabasePath,ByteUtil.encode(ip));
    }

    @Override
    public List<NodePO> queryAllNodeList(){
        List<NodePO> list = new ArrayList<>();
        //获取所有
        List<byte[]> bytesNodeEntityList = KvDBUtil.get(nodeDatabasePath,1,100000000);
        if(bytesNodeEntityList != null){
            for(byte[] bytesNodeEntity:bytesNodeEntityList){
                NodePO nodePO = JsonUtil.fromJson(ByteUtil.decodeToUtf8String(bytesNodeEntity), NodePO.class);
                list.add(nodePO);
            }
        }
        return list;
    }
}
