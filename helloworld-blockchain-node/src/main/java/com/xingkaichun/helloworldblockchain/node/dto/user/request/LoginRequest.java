package com.xingkaichun.helloworldblockchain.node.dto.user.request;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import lombok.Data;

@Data
public class LoginRequest {

    private UserDto userDto;
}
