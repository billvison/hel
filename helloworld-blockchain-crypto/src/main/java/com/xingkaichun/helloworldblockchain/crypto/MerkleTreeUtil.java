package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.Arrays;

import java.util.ArrayList;
import java.util.List;


/**
 * 默克尔树工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class MerkleTreeUtil {

    /**
     * 计算默克尔树根
     */
    public static String calculateMerkleTreeRootReturnHexadecimal(List<String> dataList) {
        List<byte[]> bytesDataList = new ArrayList<>(dataList.size());
        for(String data:dataList){
            bytesDataList.add(HexUtil.hexStringToBytes(data));
        }
        byte[] bytesMerkleTreeRoot = calculateMerkleTreeRoot(bytesDataList);
        return HexUtil.bytesToHexString(bytesMerkleTreeRoot);
    }

    /**
     * 计算默克尔树根
     * https://en.bitcoin.it/wiki/Protocol_documentation#Merkle_Trees
     * https://blog.csdn.net/jason_cuijiahui/article/details/79011118
     * https://www.cnblogs.com/web-java/articles/5544093.html
     * 摘抄于bitcoinj-core-0.15.8.jar!\org\bitcoinj\core\Block.java MerkleRoot()方法
     *
     * @author 邢开春 409060350@qq.com
     */
    public static byte[] calculateMerkleTreeRoot(List<byte[]> dataList) {
        List<byte[]> tree = new ArrayList<>(dataList);
        int size = tree.size();
        int levelOffset = 0;
        for (int levelSize = size; levelSize > 1; levelSize = (levelSize + 1) / 2) {
            for (int left = 0; left < levelSize; left += 2) {
                int right = Math.min(left + 1, levelSize - 1);
                byte[] leftBytes = tree.get(levelOffset + left);
                byte[] rightBytes = tree.get(levelOffset + right);
                tree.add(SHA256Util.doubleDigest(Arrays.concatenate(leftBytes, rightBytes)));
            }
            levelOffset += levelSize;
        }
        return tree.get(tree.size()-1);
    }
}