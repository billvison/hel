package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request;

import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;
import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class SetConfigurationRequest {

    private ConfigurationDto configurationDto;
}
