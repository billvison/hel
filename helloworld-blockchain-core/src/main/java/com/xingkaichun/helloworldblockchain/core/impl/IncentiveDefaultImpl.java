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
 * '系统币'总额：总额设置为一万亿枚，币不允许被分割，币只有1个币的说法，没有0.1个币的说法。
 * '激励分配'的目的：①维护区块链网络长久存在。②促进区块链世界的发展。③促进计算机世界的发展。④为世界带来欢乐与便利。
 * '激励分配'的原则：①重点奖励，对少数有突出贡献的人进行重奖。②共同富裕，多数人可以获得奖励。
 * '激励'奖励群体：①创始团队②矿工③开发者社区④基金会⑤建设者。分配比例为1：20：50：20：9。
 * '激励'的来源有三种：①系统最初派发的一万亿，以下用'最初币'代替②交易手续费③未花费交易输出的存储费
 * '激励'领取方式：矿工的奖励由矿工领取，其它群体，由矿工代领，每10000个区块可以领取一次，每次领取适宜数额的激励，然后在系统外分配。
 * 其它：除了矿工外，任何激励的使用需要列出详细账单，账单必须要公开，要经得起推敲与质疑。
 * 一个人允许拥有多个角色。他既可以是创始团队成员、也可以是矿工、还可以是开发者社区的一员。
 *
 *
 * '最初币'的分配
 * 创始团队的奖励，占总激励的1%。没有创始团队，啥都没有了，不可不奖励。
 * 约定：创始团队每10000个区块，可以释放1次，一次最多释放1 0000 0000(一亿枚)个激励。大约19年释放完毕。
 * 该项目完全由邢开春1人历时2年独立完成。创始团队特别感谢其贡献，创始团队内部也没有任何意见，将会将'创始团队'的奖励全部发放给其人。
 * 创始团队与开发者社区的关系：最初是没有开发者社区存在的，只有创始团队，但是作为目标是世界级的区块链领域的开源项目，怎么能不是去中心化的呢？
 * 创始团队会慢慢地将权利移交出去，在适当的时候，会彻底退出，社区全部转交给开发者维护。创始团队与开发者社区并列存在时，以创始团队为主导。
 *
 * 矿工的奖励，占总激励的20%。矿工是维护区块链网络的基石，不可不奖励。它的分配策略请看具体的代码。
 *
 * 开发者社区的奖励，占总激励的50%。开发者是探究区块链技术发展的先锋队，不可不奖励。
 * 约定：开发者社区每10000个区块，可以释放1次，一次最多释放10 0000 0000(十亿枚)个激励。大约95年释放完毕。
 * 能看到这行代码的开发者，快来贡献你的一份力量吧，区块链社区需要你，社区地址 https://github.com/xingkaichun/HelloworldBlockchain 。
 * 开发者分为初级开发者、中级开发者、高级开发者、荣誉开发者四个开发者群体。
 * 初级开发者10000名，每人每次领取   6 0000，有退出机制与重入机制。暂时不满员，期待各路英雄的加入。
 * 中级开发者1000 名，每人每次领取  20 0000，有退出机制与重入机制。暂时不满员，期待各路英雄的加入。
 * 高级开发者100  名，每人每次领取 100 0000，有退出机制与重入机制。暂时不满员，期待各路英雄的加入。
 * 荣誉开发者：每人只能领取一次奖励，每次1 0000 0000个，但是称号永久存在。有突出贡献的开发者，都可以领取荣誉开发者称号。所谓突出以创始团队贡献为基准。
 * 开发者动态奖池，以上分配剩余的属于动态奖池，各级开发中的部分人可能贡献更大，但是得到得奖励却很少，这里用以弥补。每人每次领取范围为50 0000 到 500 0000。
 *
 * 基金会的奖励，占总激励的20%。奖励暂时不启动。github 50万star在启动。
 * 基金会：与现实世界进行交互，用于投资产生盈利，所得盈利看作"最初币"进行分配即可，以支持持续发展项目。
 *
 * 建设者的奖励，占总激励的9%。奖励暂时不启动。github 100万star在启动。
 * 建设者：对计算机世界建设有意义的人。
 * 对建设者的奖励是纯支出不要任何回馈的激励分配。
 * 开源者、底层建设者等等，奖励暂时不启动。如果启动，一定要公平公正公开在社区内得到广泛认同。
 *
 * 交易手续费的分配：看作"最初币"进行分配即可。
 * 未花费交易输出的存储费的分配：看作"最初币"进行分配即可。
 *
 * @author 邢开春 409060350@qq.com
 */
public class IncentiveDefaultImpl extends Incentive {

    @Override
    public long incentiveAmount(BlockchainDatabase blockchainDataBase, Block block) {
        //给予矿工的挖矿津贴
        long minerSubsidy = getMinerSubsidy(block);
        //给予矿工的交易手续费
        long minerFee = (long) (BlockTool.getBlockFee(block)*0.2);
        //给予其他团体的激励 ，由矿工代领取。
        long otherTeamIncentiveAmount = getOtherTeamSubsidy(block);
        //总的激励
        long total = minerSubsidy + minerFee + otherTeamIncentiveAmount;
        return total;
    }

    @Override
    public String incentiveAddress(BlockchainDatabase blockchainDataBase, Block block) {
        if(block.getHeight() == 1){
            //TODO 上线时
            return AccountUtil.randomAccount().getAddress();
        }else if(block.getHeight() == 10001){
            //TODO 上线时
            return AccountUtil.randomAccount().getAddress();
        }else if(block.getHeight() == 20001){
            //TODO 上线时
            return AccountUtil.randomAccount().getAddress();
        }else if(block.getHeight() == 30001){
            //TODO 上线时
            return AccountUtil.randomAccount().getAddress();
        }else if(block.getHeight() == 40001){
            //TODO 上线时
            return AccountUtil.randomAccount().getAddress();
        }else if(block.getHeight() == 50001){
            //TODO 上线时
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
                LogUtil.debug("区块数据异常，矿工地址异常。");
                return false;
            }
        }
        return true;
    }

    /**
     * '最初币'派发给矿工的激励分配策略
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

    private long getOtherTeamSubsidy(Block block) {
        if(block.getHeight() == 1){
            // 创始团队的奖励 + 开发者的奖励
            return 1L * 10000L * 10000L + 10L * 10000L * 10000L;
        }else if(block.getHeight() == 10001){
            return 1L * 10000L * 10000L + 10L * 10000L * 10000L;
        }else if(block.getHeight() == 20001){
            return 1L * 10000L * 10000L + 10L * 10000L * 10000L;
        }else if(block.getHeight() == 30001){
            return 1L * 10000L * 10000L + 10L * 10000L * 10000L;
        }else if(block.getHeight() == 40001){
            return 1L * 10000L * 10000L + 10L * 10000L * 10000L;
        }else if(block.getHeight() == 50001){
            return 1L * 10000L * 10000L + 10L * 10000L * 10000L;
        }
        return 0L;
    }
}
