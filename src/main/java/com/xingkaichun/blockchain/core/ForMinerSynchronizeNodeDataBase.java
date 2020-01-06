package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;


/**
 * 区块链是一个分布式的数据库。
 * 当其它节点的区块链区块长度大于本节点区块链长度时，本节点应该同步其它节点的区块数据。
 * 本类的主要功能就是存储其它节点的区块数据，以供本节点区块链使用这些区块数据来进行同步其它区块链。
 *
 * 需要同步多少个区块？
 * 假设本节点A区块链(A的区块个数是M个)需要同步B节点区块链(B的区块个数是(M+N)个)的区块数据。
 * 若A与B从第(M-P)个区块分叉(也就是前(M-P-1)个区块数据一致，A与B区块链从第((M-P-1)+1)个区块开始不一致了)，
 * 那么A需要同步B区块链(N+P+1)个区块的数据。
 * 特殊情况A与B不分叉，即P=-1，A区块链的前M个区块数据和B的前M个区块数据完全一致，A只需要同步B区块链后N个区块的数据即可。
 *
 * A同步B区块链分为两个阶段：
 * 数据传输 将B节点的数据分批次传输到A节点
 * 真正同步 A区块链对传输过来的区块数据进行校验检测。
 * 若校验失败，丢弃传输过来的区块数据。
 * 若校验通过，将传输过来的区块数据整合进自身区块链，A的区块链将变得更长。
 *
 * A同步B区块链时，需要同步的区块个数太多，一次性网络传输不完，不能把数据一次性加载到内存。
 * 因此本类传输的单位是Block。
 *
 * 假设P=10000，在A同步B区块链的区块数据的过程中，B一次传输100个区块。
 * 每当A接收到B的100个区块时，A立刻尝试把同步的区块放入A的区块链上。
 * 当第一批100个区块传输完毕，因为100<10000，所以A认为这一百个区块太少，A不能形成更长的区块链。
 * 当第二批100个区块传输完毕，因为100+100<10000，所以A认为这这两批次区块太少，A不能形成更长的区块链。
 * ......
 * 因此应当有一个标识，用来代表本节点区块链可以进行真正的同步操作了。
 * A对B可以进行'真正同步'操作的时机：已同步B的区块高度大于自身区块高度。
 * 但是，为了简单，我们选择当B的数据完全传输完毕，A再进行真正的同步操作。
 * 所以，在这里，人为的增加了一个代表数据传输完毕的标识。
 *
 * 本地区块链可能需要同步多个节点区块链的数据，因此本类需要能够存放多个节点的区块链数据，并且保证它们互不干扰。
 *
 * 本类的正确使用方式
 * 获取一个有数据传输完毕标识的节点ID。
 * 根据节点ID可获取传输过来的完整数据。(循环获取节点下一个BlockChainSegement，直至获取结果为null)。在这一步中，
 * 因为获取到了传输的数据，所以可以做自己的业务逻辑了。
 * 使用完毕，清除节点ID的数据传输完毕标识，删除节点ID传输数据。
 */
public interface ForMinerSynchronizeNodeDataBase {

    /**
     * 保存节点(nodeId)传输过来的数据
     */
    boolean addBlock(String nodeId, Block block) throws Exception ;

    /**
     * 获取节点(nodeId)传输过来的数据，返回结果不为null，代表可以继续调用此方法，继续获取数据。
     */
    Block getNextBlock(String nodeId) throws Exception ;

    /**
     * 获取一个有数据传输完成标识的节点ID
     */
    String getDataTransferFinishFlagNodeId() throws Exception ;

    /**
     * 给节点(nodeId)添加数据传输完成的标识。
     */
    void addDataTransferFinishFlag(String nodeId) throws Exception ;

    /**
     * 清除节点(nodeId)的数据传输完成标识
     */
    void clearDataTransferFinishFlag(String nodeId) throws Exception ;

    /**
     * 删除节点(nodeId)传输过来的数据。
     */
    void deleteTransferData(String nodeId) throws Exception ;

    /**
     * 节点(nodeId)有数据传输完成的标识吗？
     */
    boolean hasDataTransferFinishFlag(String nodeId);
}
