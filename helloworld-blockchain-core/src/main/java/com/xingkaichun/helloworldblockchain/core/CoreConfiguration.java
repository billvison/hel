package com.xingkaichun.helloworldblockchain.core;

/**
 * Core配置: BlockchainCore的配置。
 * 该类对BlockchainCore模块的配置进行统一管理。
 * 在这里可以持久化配置信息。
 * 理论上，BlockchainCore模块的任何地方需要配置参数，都可以从该类获取。
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class CoreConfiguration {

    public abstract String getCorePath();
    public abstract boolean isMinerActive();
    public abstract void activeMiner() ;
    public abstract void deactiveMiner() ;
    public abstract long getMineTimestampPerRound();
}
