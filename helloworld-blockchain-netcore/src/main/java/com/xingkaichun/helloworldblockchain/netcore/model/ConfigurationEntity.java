package com.xingkaichun.helloworldblockchain.netcore.model;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ConfigurationEntity {

    private String confKey;
    private String confValue;




    //region get set

    public String getConfKey() {
        return confKey;
    }

    public void setConfKey(String confKey) {
        this.confKey = confKey;
    }

    public String getConfValue() {
        return confValue;
    }

    public void setConfValue(String confValue) {
        this.confValue = confValue;
    }

    //endregion
}
