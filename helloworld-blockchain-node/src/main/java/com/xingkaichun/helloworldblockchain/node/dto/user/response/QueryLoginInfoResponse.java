package com.xingkaichun.helloworldblockchain.node.dto.user.response;

import lombok.Data;

@Data
public class QueryLoginInfoResponse {

    private LoginUserDto userDto;


    @Data
    public static class LoginUserDto {
        private String userName;
    }
}
