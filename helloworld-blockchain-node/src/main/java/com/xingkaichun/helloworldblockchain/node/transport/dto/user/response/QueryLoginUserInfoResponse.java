package com.xingkaichun.helloworldblockchain.node.transport.dto.user.response;

import lombok.Data;

@Data
public class QueryLoginUserInfoResponse {

    private LoginUserDto userDto;


    @Data
    public static class LoginUserDto {
        private int userId;
        private String userName;
    }
}
