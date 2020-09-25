package com.xingkaichun.helloworldblockchain.core.model.synchronizer;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;

import java.io.Serializable;
/**
 * 节点同步时，接收传输过来的区块，可以增加部分信息到区块上暂时保存下来，等待使用。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SynchronizerBlockDTO extends BlockDTO implements Serializable {

    //区块高度
    private long height;


    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }
}
