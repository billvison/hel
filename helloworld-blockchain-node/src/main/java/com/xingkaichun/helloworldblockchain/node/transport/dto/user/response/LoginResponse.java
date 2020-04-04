package com.xingkaichun.helloworldblockchain.node.transport.dto.user.response;

import lombok.Data;

@Data
public class LoginResponse {

    private LoginUserDto userDto;


    @Data
    public static class LoginUserDto {
        private int userId;
        private String userName;
    }
}
