package com.xingkaichun.helloworldblockchain.node.dto.user.response;

import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryLoginUserInfoResponse {

    private LoginUserDto userDto;


    @Data
    public static class LoginUserDto {
        private int userId;
        private String userName;
    }
}
