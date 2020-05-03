package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response;

import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class GetConfigurationByConfigurationKeyResponse {

    private ConfigurationDto configurationDto;




    //region get set

    public ConfigurationDto getConfigurationDto() {
        return configurationDto;
    }

    public void setConfigurationDto(ConfigurationDto configurationDto) {
        this.configurationDto = configurationDto;
    }


    //endregion
}
