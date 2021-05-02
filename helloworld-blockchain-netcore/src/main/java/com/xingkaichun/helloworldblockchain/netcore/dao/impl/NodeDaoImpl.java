package com.xingkaichun.helloworldblockchain.netcore.dao.impl;

import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.NodeDao;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
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

    public NodeDaoImpl(String blockchainDataPath) {
        this.nodeDatabasePath = FileUtil.newPath(blockchainDataPath, NODE_DATABASE_NAME);
    }

    @Override
    public NodeEntity queryNode(String ip){
        List<NodeEntity> nodeEntityList = queryAllNodeList();
        if(nodeEntityList != null){
            for(NodeEntity n:nodeEntityList){
                if(StringUtil.isEquals(ip,n.getIp())){
                    return n;
                }
            }
        }
        return null;
    }

    @Override
    public void addNode(NodeEntity node){
        KvDBUtil.put(nodeDatabasePath,ByteUtil.encode(node.getIp()), ByteUtil.encode(JsonUtil.toJson(node)));
    }

    @Override
    public void updateNode(NodeEntity node){
        KvDBUtil.put(nodeDatabasePath,ByteUtil.encode(node.getIp()),ByteUtil.encode(JsonUtil.toJson(node)));
    }

    @Override
    public void deleteNode(String ip){
        KvDBUtil.delete(nodeDatabasePath,ByteUtil.encode(ip));
    }

    @Override
    public List<NodeEntity> queryAllNodeList(){
        List<NodeEntity> list = new ArrayList<>();
        //获取所有
        List<byte[]> bytesNodeEntityList = KvDBUtil.get(nodeDatabasePath,1,100000000);
        if(bytesNodeEntityList != null){
            for(byte[] bytesNodeEntity:bytesNodeEntityList){
                NodeEntity nodeEntity = JsonUtil.fromJson(ByteUtil.decodeToUtf8String(bytesNodeEntity),NodeEntity.class);
                list.add(nodeEntity);
            }
        }
        return list;
    }
}
