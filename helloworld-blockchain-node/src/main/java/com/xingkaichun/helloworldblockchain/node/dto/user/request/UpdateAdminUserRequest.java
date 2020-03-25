package com.xingkaichun.helloworldblockchain.node.dto.user.request;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import lombok.Data;

@Data
public class UpdateAdminUserRequest {

    private UserDto userDto;
}
