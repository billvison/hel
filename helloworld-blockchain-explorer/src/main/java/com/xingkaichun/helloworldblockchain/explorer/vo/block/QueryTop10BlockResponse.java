package com.xingkaichun.helloworldblockchain.explorer.vo.block;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryTop10BlockResponse {

    private List<BlockView> blockViewList ;



    //region get set
    public List<BlockView> getBlockViewList() {
        return blockViewList;
    }

    public void setBlockViewList(List<BlockView> blockViewList) {
        this.blockViewList = blockViewList;
    }
    //endregion



    public static class BlockView {

        private long height;
        private String blockSize;
        private long transactionCount;
        private long minerIncentiveValue;
        private String time;
        private String hash;

        public long getHeight() {
            return height;
        }

        public void setHeight(long height) {
            this.height = height;
        }

        public String getBlockSize() {
            return blockSize;
        }

        public void setBlockSize(String blockSize) {
            this.blockSize = blockSize;
        }

        public long getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(long transactionCount) {
            this.transactionCount = transactionCount;
        }

        public long getMinerIncentiveValue() {
            return minerIncentiveValue;
        }

        public void setMinerIncentiveValue(long minerIncentiveValue) {
            this.minerIncentiveValue = minerIncentiveValue;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }
    }
}
