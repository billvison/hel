package com.xingkaichun.helloworldblockchain.netcore.entity;

/**
 *
 * @author 邢开春
 */
public class ConfigurationEntity {

    private String confKey;
    private String confValue;

    public ConfigurationEntity() {
    }

    public ConfigurationEntity(String confKey, String confValue) {
        this.confKey = confKey;
        this.confValue = confValue;
    }
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
