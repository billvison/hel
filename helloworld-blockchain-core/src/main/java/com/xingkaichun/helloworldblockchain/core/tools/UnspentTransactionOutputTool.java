package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.transaction.UnspentTransactionOutput;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;

/**
 * 未花费输出工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class UnspentTransactionOutputTool {

    /**
     * 未花费交易输出 是否有足够的余额去支付 存储费
     */
    public static boolean isUnspentTransactionOutputHasEnoughStoreFee(UnspentTransactionOutput unspentTransactionOutput,long blockHeight) {
        //存储费用
        long unspentTransactionOutputStoreSpend = unspentTransactionOutputStoreFee(unspentTransactionOutput,blockHeight);
        //余额不足以支付地址存储费用
        if(unspentTransactionOutput.getValue()<=unspentTransactionOutputStoreSpend){
            LogUtil.debug("未花费输出数据异常，未花费输出不足以支付存储费用。");
            return false;
        }
        return true;
    }

    /**
     * 未花费交易输出 被消耗时 需要付出的 的存储费
     * 未花费交易输出数据的无限膨胀问题是区块链的难题之一，一笔未花费输出产生后，会一直占用节点的磁盘
     * ，直至其被花费掉，才能在磁盘中将其删除
     * ，故设计系统时，设计了每笔[未花费交易输出]占用区块链网络的资源，需要付费的模型。
     * 付出的存储费用与三个变量相关：
     * 每笔[未花费输出]在每个区块高度的费用
     * 未花费输出产生的区块高度
     * 未花费输出被消耗时的高度
     */
    public static long unspentTransactionOutputStoreFee(UnspentTransactionOutput unspentTransactionOutput,long blockHeight) {
        //支付地址存储费用
        long inputBlockHeight = unspentTransactionOutput.getBlockHeight();
        return GlobalSetting.StoreFeeConstant.getStoreFee(inputBlockHeight,blockHeight);
    }
}
