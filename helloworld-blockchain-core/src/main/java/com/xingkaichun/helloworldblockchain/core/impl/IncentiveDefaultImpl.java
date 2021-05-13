package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.Incentive;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;

/**
 * 经过创始团队的深入热烈的探讨，最终确定了激励分配方案。
 *
 * '系统币'总额：总额设置为一万亿枚，不能小数分割，任何时刻系统币总额不允许大于一万亿。
 * '激励分配'的目的：①维护区块链网络长久存在。②促进区块链世界的发展。③促进信息时代的发展。④让更多的人幸福起来。⑤让更多的人自由起来。
 * '激励分配'的原则：①重点奖励，对少数有突出贡献的人进行重奖。②共同富裕，多数人可以获得奖励。
 * '激励'奖励目标群体：①创始团队②矿工③开发者社区④基金会⑤建设者。分配比例为1：20：30：20：29。
 * '激励'的来源有三种:①系统最初派发的一万亿②交易手续费③未花费交易输出的存储费
 * '激励'领取方式:矿工每挖到区块时就自动分配，其它群体，每10000个区块可以领取一次，由矿工代领一次，每次领取适宜数额的激励，然后在系统外分配。
 * 除了矿工外，任何激励的使用需要列出详细账单，账单必须要公开，要经得起推敲与质疑。
 * 一个人允许拥有多个角色。他既可以是创始团队成员、也可以是矿工、还可以是开发者社区的一员。
 *
 *
 * "激励来源①系统最初派发的一万亿"的分配
 * 创始团队的奖励，占总激励的1%。没有创始团队，啥都没有了，不可不奖励。
 * 约定：创始团队每10000个区块，可以释放1次，一次最多释放1 0000 0000(一亿枚)个激励。大约19年释放完毕。
 * 该项目完全由邢开春1人历时2年独立完成。创始团队特别感谢其贡献，创始团队内部也没有任何意见，将会将'创始团队'的奖励全部发放给其人。
 * 创始团队与开发者社区的关系：最初是没有开发者社区存在的，只有创始团队，但是作为目标是世界级的区块链领域的开源项目，怎么能不是去中心化的呢？
 * 创始团队会慢慢地将权利移交出去，在适当的时候，会彻底退出，社区全部转交给开发者维护。
 *
 * 矿工的奖励，占总激励的20%。矿工是维护区块链网络的基石，不可不奖励。它的分配策略请看代码处。
 *
 * 开发者社区的奖励，占总激励的30%。开发者是探究区块链技术发展的先锋队，不可不奖励。
 * 约定：开发者社区每10000个区块，可以释放1次，一次最多释放10 0000 0000(十亿枚)个激励。大约57年释放完毕。
 * 能看到这行代码的开发者，快来贡献你的一份力量吧，区块链社区需要你，社区地址 https://github.com/xingkaichun/HelloworldBlockchain 。
 * 开发者分为初级开发者、中级开发者、高级开发者、荣誉开发者四个开发者群体，四个开发者群体各拿25%开发者的奖励。
 * 初级开发者10000名，每人每次最多领取 10 0000 0000*25%/10000  =     4000，有退出机制与重入机制。
 * 中级开发者1000 名，每人每次最多领取 10 0000 0000*25%/1000   =   4 0000，有退出机制与重入机制。
 * 高级开发者100  名，每人每次最多领取 10 0000 0000*25%/100    =  40 0000，有退出机制与重入机制。现阶段有心参与的开发，至少获得该激励。
 * 初期创始团队会重点审核开发者有无资格成为高级开发者，后期开发者团体自己审核开发者是不是有资质成为高级开发者。
 * 荣誉开发者：每人只能领取一次奖励，每次1 0000 0000(一亿枚)个，但是称号永久存在。有突出贡献的开发者，都可以领取荣誉开发者称号。可能只有创始团队才能领走一个荣誉开发者称号了。
 *
 * 基金会的奖励，占总激励的20%。github 100万star在启动。
 * 基金会：用于投资产生盈利持续发展项目的资金。奖励暂时不启动。
 *
 * 建设者的奖励，占总激励的29%。github 100万star在启动。
 * 建设者：对计算机世界建设有意义的人。
 * 开源者、底层建设者等等，奖励暂时不启动。如果启动，一定要公平公正公开在社区内得到广泛认同。
 *
 * 激励来源②交易手续费的分配：参考以上比例进行分配。
 * 激励来源③未花费交易输出的存储费的分配：参考以上比例进行分配。
 *
 * @author 邢开春 409060350@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    @Override
    public long incentiveAmount(BlockchainDatabase blockchainDataBase, Block block) {
        //给予矿工的交易手续费
        long minerFee = (long) (BlockTool.getBlockFee(block)*0.2);
        //给予矿工的挖矿津贴
        long minerSubsidy = getMinerSubsidy(block);
        //给予其他团体的挖矿津贴，由矿工代领取。
        long otherIncentiveAmount = getOtherSubsidy(block);
        //总的挖矿奖励
        long total = minerSubsidy + minerFee + otherIncentiveAmount;
        return total;
    }

    @Override
    public String incentiveAddress(BlockchainDatabase blockchainDataBase, Block block) {
        if(block.getHeight() == 1){
            return AccountUtil.randomAccount().getAddress();
        }
        return null;
    }

    @Override
    public boolean isIncentiveRight(BlockchainDatabase blockchainDataBase, Block block) {
        long writeIncentiveValue = BlockTool.getMinerIncentiveValue(block);
        long targetIncentiveValue = incentiveAmount(blockchainDataBase,block);
        if(writeIncentiveValue != targetIncentiveValue){
            LogUtil.debug("区块数据异常，挖矿奖励数据异常。");
            return false;
        }
        String writeAddress = block.getTransactions().get(0).getOutputs().get(0).getAddress();
        String targetAddress = incentiveAddress(blockchainDataBase,block);
        if(!StringUtil.isNullOrEmpty(targetAddress)){
            if(!StringUtil.isEquals(targetAddress,writeAddress)){

            }
        }
        return true;
    }

    /**
     * 系统最初'一万亿'派发给矿工的激励分配策略
     *
     * 随着时间的过去，矿工越来越多了，所以发放的奖励也多了。
     *                       前10000*1个区块，每个区块奖励10000*1个
     * 如果条件不满足前一条，前10000*2个区块，每个区块奖励10000*2个
     * 如果条件不满足前一条，前10000*3个区块，每个区块奖励10000*3个
     * 如果条件不满足前一条，前10000*n(n小于等于40)个区块，每个区块奖励10000*n个
     *
     * 随着时间的过去，固定奖励快没了，降低区块奖励，缓慢的降低固定奖励，给矿工一个缓冲期
     * 如果条件不满足前一条，前10000*41个区块，每个区块奖励10000*(40-(41-41))个
     * 如果条件不满足前一条，前10000*42个区块，每个区块奖励10000*(40-(42-41))个
     * 如果条件不满足前一条，前10000*43个区块，每个区块奖励10000*(40-(43-41))个
     * 如果条件不满足前一条，前10000*n(n小于等于80)个区块，每个区块奖励10000*(40-(n-41))个
     *
     * 剩余的矿工奖励也要分配完
     * 如果条件不满足前一条，前10000*n(n小于等于440)个区块，每个区块奖励10000个
     *
     * 最后不再奖励了
     */
    private long getMinerSubsidy(Block block) {
        if(block.getHeight() <= 10000*40){
            return 10000L * ((block.getHeight()-1)/10000+1);
        }else if(block.getHeight() <= 10000*40+10000*40){
            return 10000L * (40-((block.getHeight()-1)/10000-41));
        }else if(block.getHeight() <= 10000*40+10000*40+10000*320){
            return 10000L;
        }else {
            return 0L;
        }
    }

    private long getOtherSubsidy(Block block) {
        if(block.getHeight() == 1){
            /**
             * 大约(创始团队区块1到区块20000的奖励) + 大约(开发者区块1到区块20000的奖励) + 矿工的第一个区块的奖励
             * 为什么是大约，因为还没有计算激励来源②、激励来源③
             * 这里是借用矿工将分配给创始团队与开发者前20000个区块的奖励拿出来，开发者奖励给哪些人，会看github的贡献，任何人可查。
             * 创始团队、开发者的奖励每隔一段时间就可以借用矿工之手将币拿出来一部分，这样免得避免创始团队、开发者提前拿到一大笔币。
             */
            return 2L * 10000L * 10000L + 20L * 10000L * 10000L + 10000L;
        }
        return 0L;
    }
}
