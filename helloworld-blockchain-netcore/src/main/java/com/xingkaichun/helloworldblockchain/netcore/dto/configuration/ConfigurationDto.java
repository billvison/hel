package com.xingkaichun.helloworldblockchain.netcore.dto.configuration;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ConfigurationDto {

    private String confKey;
    private String confValue;

    public ConfigurationDto() {
    }

    public ConfigurationDto(String confKey, String confValue) {
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
