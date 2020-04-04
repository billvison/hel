package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response;

import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;
import lombok.Data;

@Data
public class GetConfigurationByConfigurationKeyResponse {

    private ConfigurationDto configurationDto;
}
