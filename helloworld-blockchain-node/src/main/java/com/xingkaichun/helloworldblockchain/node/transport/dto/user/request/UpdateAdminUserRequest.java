package com.xingkaichun.helloworldblockchain.node.transport.dto.user.request;

import com.xingkaichun.helloworldblockchain.node.transport.dto.user.UserDto;
import lombok.Data;

@Data
public class UpdateAdminUserRequest {

    private UserDto userDto;
}
