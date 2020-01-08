package com.xingkaichun.blockchain.core;

public interface Coordinator {


    //region 全局控制
    /**
     * 启动
     */
    void run() throws Exception ;
    /**
     * 暂停所有
     */
    void pause() throws Exception;
    /**
     * 恢复所有
     */
    void resume() throws Exception;

    boolean isActive() throws Exception;
    //endregion
}
