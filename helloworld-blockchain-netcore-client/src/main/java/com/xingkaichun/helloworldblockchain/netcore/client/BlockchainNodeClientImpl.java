package com.xingkaichun.helloworldblockchain.netcore.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainNodeClientImpl implements BlockchainNodeClient {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainNodeClientImpl.class);

    private Gson gson;
    private NodeDTO node;

    public BlockchainNodeClientImpl(NodeDTO node) {
        this.node = node;
        this.gson = new Gson();
    }

    @Override
    public String submitTransaction(TransactionDTO transactionDTO) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(), GlobalSetting.DEFAULT_PORT, API.POST_TRANSACTION);
            String html = NetUtil.jsonGetRequest(url,transactionDTO);
            return html;
        } catch (Exception e) {
            logger.debug(String.format("提交交易[%s]至节点[%s:%d]出现异常",gson.toJson(transactionDTO),node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    public PingResponse pingNode(PingRequest request) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),GlobalSetting.DEFAULT_PORT, API.PING);
            String html = NetUtil.jsonGetRequest(url,request);
            Type jsonType = new TypeToken<PingResponse>() {}.getType();
            PingResponse pingResponse = gson.fromJson(html,jsonType);
            return pingResponse;
        } catch (Exception e) {
            logger.debug(String.format("Ping节点[%s:%d]出现异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public BlockDTO getBlock(long blockHeight) {
        try {
            String url = String.format("http://%s:%d%s?height=%s",node.getIp(),GlobalSetting.DEFAULT_PORT, API.GET_BLOCK,blockHeight);
            String html = NetUtil.jsonGetRequest(url,null);
            Type jsonType = new TypeToken<BlockDTO>() {}.getType();
            BlockDTO block = gson.fromJson(html,jsonType);
            return block;
        } catch (Exception e) {
            logger.debug(String.format("在节点[%s:%d]查询区块出现异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public String psotBlock(BlockDTO blockDTO) {
        try {
            String url = String.format("http://%s:%d%s",node.getIp(),GlobalSetting.DEFAULT_PORT, API.POST_BLOCK);
            String psotBlockResponse = NetUtil.jsonGetRequest(url,blockDTO);
            return psotBlockResponse;
        } catch (Exception e) {
            logger.debug(String.format("Ping节点[%s:%d]出现异常",node.getIp(),GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }
}