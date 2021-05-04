package com.xingkaichun.helloworldblockchain.core;

/**
 * 配置数据库
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
