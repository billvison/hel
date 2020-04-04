package com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.adminconsole.ConfigurationDto;
import lombok.Data;

@Data
public class SetConfigurationRequest {

    private ConfigurationDto configurationDto;
}
