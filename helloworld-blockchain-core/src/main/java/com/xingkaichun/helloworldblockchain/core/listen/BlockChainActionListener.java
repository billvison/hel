package com.xingkaichun.helloworldblockchain.core.listen;

import java.util.List;

/**
 * 监听核心区块链区块增、删的动作
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public interface BlockChainActionListener {

    void addOrDeleteBlock(List<BlockChainActionData> blockChainActionDataList);
}
