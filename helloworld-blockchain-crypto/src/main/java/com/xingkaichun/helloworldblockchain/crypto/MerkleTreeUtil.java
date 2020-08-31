package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.Arrays;

import java.util.ArrayList;
import java.util.List;


/**
 * 默克尔树工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class MerkleTreeUtil {

    /**
     * https://en.bitcoin.it/wiki/Protocol_documentation#Merkle_Trees
     * https://blog.csdn.net/jason_cuijiahui/article/details/79011118
     * https://www.cnblogs.com/web-java/articles/5544093.html
     * 摘抄于bitcoinj-core-0.15.8.jar!\org\bitcoinj\core\Block.java MerkleRoot()方法
     *
     * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
     */
    public static byte[] calculateMerkleRootByHash(List<byte[]> hashList) {
        if(hashList == null || hashList.size() == 0){
            throw new RuntimeException("hash数组不能为空");
        }
        if(hashList.size() == 1){
            return hashList.get(0);
        }
        List<byte[]> tree = new ArrayList<>(hashList);
        int size = tree.size();
        int levelOffset = 0;
        for (int levelSize = size; levelSize > 1; levelSize = (levelSize + 1) / 2) {
            for (int left = 0; left < levelSize; left += 2) {
                int right = Math.min(left + 1, levelSize - 1);
                byte[] leftBytes = tree.get(levelOffset + left);
                byte[] rightBytes = tree.get(levelOffset + right);
                //TODO 外面一次diget 这里两次 合适吗？
                tree.add(SHA256Util.digestTwice(Arrays.concatenate(leftBytes, rightBytes)));
            }
            levelOffset += levelSize;
        }
        return tree.get(tree.size()-1);
    }
}