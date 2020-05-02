package com.xingkaichun.helloworldblockchain.node.dto.adminconsole;

import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class ConfigurationDto {

    private String confKey;
    private String confValue;

    public ConfigurationDto() {
    }

    public ConfigurationDto(String confKey, String confValue) {
        this.confKey = confKey;
        this.confValue = confValue;
    }
}
