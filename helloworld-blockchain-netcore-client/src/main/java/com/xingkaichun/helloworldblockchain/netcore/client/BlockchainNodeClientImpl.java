package com.xingkaichun.helloworldblockchain.netcore.client;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.API;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.NetUtil;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainNodeClientImpl implements BlockchainNodeClient {

    private String ip;

    public BlockchainNodeClientImpl(String ip) {
        this.ip = ip;
    }

    @Override
    public String postTransaction(TransactionDTO transactionDTO) {
        try {
            String url = String.format("http://%s:%d%s",ip, GlobalSetting.DEFAULT_PORT, API.POST_TRANSACTION);
            String html = NetUtil.jsonGetRequest(url,transactionDTO);
            return html;
        } catch (Exception e) {
            LogUtil.error(String.format("提交交易[%s]至节点[%s:%d]出现异常", JsonUtil.toJson(transactionDTO),ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    public String pingNode() {
        try {
            String url = String.format("http://%s:%d%s",ip,GlobalSetting.DEFAULT_PORT, API.PING);
            String html = NetUtil.jsonGetRequest(url,null);
            return html;
        } catch (Exception e) {
            LogUtil.error(String.format("Ping节点[%s:%d]出现异常",ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public BlockDTO getBlock(long blockHeight) {
        try {
            String url = String.format("http://%s:%d%s?height=%s",ip,GlobalSetting.DEFAULT_PORT, API.GET_BLOCK,blockHeight);
            String html = NetUtil.jsonGetRequest(url,null);
            BlockDTO block = JsonUtil.fromJson(html,BlockDTO.class);
            return block;
        } catch (Exception e) {
            LogUtil.error(String.format("在节点[%s:%d]查询区块出现异常",ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public String[] getNodes() {
        try {
            String url = String.format("http://%s:%d%s",ip,GlobalSetting.DEFAULT_PORT, API.GET_NODES);
            String html = NetUtil.jsonGetRequest(url,null);
            String[] nodes = JsonUtil.fromJson(html,String[].class);
            return nodes;
        } catch (Exception e) {
            LogUtil.error(String.format("在节点[%s:%d]查询节点列表出现异常",ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public String postBlock(BlockDTO blockDTO) {
        try {
            String url = String.format("http://%s:%d%s",ip,GlobalSetting.DEFAULT_PORT, API.POST_BLOCK);
            String psotBlockResponse = NetUtil.jsonGetRequest(url,blockDTO);
            return psotBlockResponse;
        } catch (Exception e) {
            LogUtil.error(String.format("向节点[%s:%d]提交区块出现异常",ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public String postBlockchainHeight(long blockchainHeight) {
        try {
            String url = String.format("http://%s:%d%s?height=%s",ip,GlobalSetting.DEFAULT_PORT, API.POST_BLOCKCHAIN_HEIGHT,blockchainHeight);
            String html = NetUtil.jsonGetRequest(url,null);
            return html;
        } catch (Exception e) {
            LogUtil.error(String.format("向节点[%s:%d]提交区块链高度出现异常",ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public Long getBlockchainHeight() {
        try {
            String url = String.format("http://%s:%d%s",ip,GlobalSetting.DEFAULT_PORT, API.GET_BLOCKCHAIN_HEIGHT);
            String html = NetUtil.jsonGetRequest(url,null);
            if(html == null || "".equals(html)){
                return null;
            }
            return Long.parseLong(html);
        } catch (Exception e) {
            LogUtil.error(String.format("向节点[%s:%d]获取区块链高度出现异常",ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }

    @Override
    public TransactionDTO getTransaction(long transactionHeight) {
        try {
            String url = String.format("http://%s:%d%s?height=%s",ip,GlobalSetting.DEFAULT_PORT,API.GET_TRANSACTION,transactionHeight);
            String html = NetUtil.jsonGetRequest(url,null);
            TransactionDTO transaction = JsonUtil.fromJson(html,TransactionDTO.class);
            return transaction;
        } catch (Exception e) {
            LogUtil.error(String.format("在节点[%s:%d]查询交易出现异常",ip,GlobalSetting.DEFAULT_PORT),e);
            return null;
        }
    }
}