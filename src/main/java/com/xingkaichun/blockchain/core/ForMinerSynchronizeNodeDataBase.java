package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.BlockChainSegement;


/**
 * TODO 多个区块链可以同时写入，能区分节点，要标记某个节点的数据是否同步完成，这是一个标识，提示别人可以来处理这一部分数据了，
 * 用于存放其它区块链节点的数据。
 *
 * 区块链是一个分布式的数据库。
 * 当其它节点的区块链区块长度大于本节点区块链长度时，本节点应该同步其它节点的区块数据。
 * 需要同步多少个区块？
 * 假设本节点A区块链(A的区块个数是M个)需要同步B节点区块链(B的区块个数是(M+N)个)的区块数据。
 * 若A与B从第(M-P)个区块分叉(也就是前(M-P-1)个区块数据一致，A与B区块链从第((M-P-1)+1)个区块开始不一致了)，
 * 那么A需要同步B区块链(N+P+1)个区块的数据。
 * 特殊情况A与B不分叉，即P=-1，A区块链的前M个区块数据和B的前M个区块数据完全一致，A只需要同步B区块链后N个区块的数据即可。
 *
 * 同步可能遇到的困难：
 * A同步B区块链时，需要同步的区块个数太多，一次性传输不完，不能把数据一次性加载到内存。
 *
 * A同步B区块链分为两个阶段：
 * 数据传输 将B节点的数据分批次传输到A节点
 * 真正同步 A区块链对传输过来的区块数据进行校验检测。
 * 若校验失败，丢弃传输过来的区块数据。
 * 若校验通过，将传输过来的区块数据整合进自身区块链，A的区块链将变得更长。
 *
 * 假设P=10000，在A同步B区块链的区块数据的过程中，B一次传输100个区块。
 * 没当A接收到B的100个区块时，A立刻尝试把同步的区块放入A的区块链上。
 * 当第一批100个区块传输完毕，因为100<10000，所以A认为这一百个区块太少，A不能形成更长的区块链，然后A将这100个区块丢弃。
 * 以后批次的区块因为不能被A校验通过，都将被A丢弃。
 * 若A在同步的过程尝试将同步的区块放入自身区块链，A永远不能将B的区块数据放入自身区块链上。
 * 因此A将B的区块放入自身区块链的时机应当是：已同步的区块高度大于自身区块高度。
 * 为了方便，我们选择当B的数据完全传输完毕，A再将同步过来的区块放入自身区块链。
 *
 * 本地区块链可能需要同步多个节点区块链的数据，因此本类需要能够互不干扰的存放多个区块链的数据。
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
    boolean addBlockChainSegement(String nodeId,BlockChainSegement blockChainSegement) throws Exception ;

    /**
     * 获取节点(nodeId)传输过来的数据，返回结果不为null，代表可以继续调用此方法，继续获取数据。
     */
    BlockChainSegement getNextBlockChainSegement(String nodeId) throws Exception ;

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
}
