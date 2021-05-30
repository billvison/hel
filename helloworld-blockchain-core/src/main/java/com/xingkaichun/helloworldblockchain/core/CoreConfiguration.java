package com.xingkaichun.helloworldblockchain.core;

/**
 * Core配置: BlockchainCore的配置。
 * 该类对BlockchainCore模块的配置进行统一管理。
 * 在这里可以持久化配置信息。
 * 理论上，BlockchainCore模块需要的任何配置，都可以从该对象获取。
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class CoreConfiguration {

    /**
     * BlockchainCore数据存放路径
     */
    public abstract String getCorePath();
    /**
     * 矿工是否处于激活状态？
     */
    public abstract boolean isMinerActive();
    /**
     * 激活矿工
     */
    public abstract void activeMiner() ;
    /**
     * 停用矿工
     */
    public abstract void deactiveMiner() ;
    /**
     * 矿工挖矿时间周期
     * 矿工在组装好矿之后，系统会分配矿工一个确定的时间(这个时间就叫矿工挖矿时间周期)来让矿工进行挖矿，
     * 在一个挖矿周期内，矿工并不一定能挖到矿，一个周期过去之后，矿工会停一停，歇一歇，然后重新组装矿再继续去挖矿。
     * 为什么要重新组装矿？因为如果矿一直不变，在上次矿形成之后，用户新提交的未确认交易，就没有办法打包进矿了
     * ，交易会很延迟才能得到确认。
     */
    public abstract long getMinerMineTimeInterval();
}
