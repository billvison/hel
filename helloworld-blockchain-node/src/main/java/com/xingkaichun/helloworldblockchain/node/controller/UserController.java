package com.xingkaichun.helloworldblockchain.node.controller;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.user.UserApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.dto.user.request.LoginRequest;
import com.xingkaichun.helloworldblockchain.node.dto.user.request.GetLoginUserRequest;
import com.xingkaichun.helloworldblockchain.node.dto.user.request.UpdateUserRequest;
import com.xingkaichun.helloworldblockchain.node.dto.user.request.ExitRequest;
import com.xingkaichun.helloworldblockchain.node.dto.user.response.LoginResponse;
import com.xingkaichun.helloworldblockchain.node.dto.user.response.GetLoginUserResponse;
import com.xingkaichun.helloworldblockchain.node.dto.user.response.UpdateUserResponse;
import com.xingkaichun.helloworldblockchain.node.dto.user.response.ExitResponse;
import com.xingkaichun.helloworldblockchain.node.service.UserService;
import com.xingkaichun.helloworldblockchain.node.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户功能的控制器
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Controller
@RequestMapping
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    @ResponseBody
    @RequestMapping(value = UserApiRoute.LOGIN,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<LoginResponse> login(HttpServletRequest httpServletRequest, @RequestBody LoginRequest request){
        try {
            UserDto userDto = request.getUserDto();
            if(Strings.isNullOrEmpty(userDto.getUserName())){
                return ServiceResult.createFailServiceResult("登录失败，用户名不能为空");
            }
            if(Strings.isNullOrEmpty(userDto.getPassword())){
                return ServiceResult.createFailServiceResult("登录失败，密码不能为空");
            }
            UserDto userDtoResponse = userService.login(httpServletRequest,userDto);
            if(userDtoResponse == null){
                return ServiceResult.createFailServiceResult("登录失败，请检查用户名与密码");
            }
            LoginResponse response = new LoginResponse();
            response.setUserDto(userDtoResponse);
            return ServiceResult.createSuccessServiceResult("登录成功",response);
        } catch (Exception e){
            String message = "登录失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 获取登录用户信息
     */
    @ResponseBody
    @RequestMapping(value = UserApiRoute.GET_LOGIN_USER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GetLoginUserResponse> getLoginUser(HttpServletRequest httpServletRequest, @RequestBody GetLoginUserRequest request){
        try {
            UserDto userDto = userService.getLoginUser(httpServletRequest);
            if(userDto == null){
                return ServiceResult.createFailServiceResult("获取登录信息失败，用户未登录。");
            }
            GetLoginUserResponse response = new GetLoginUserResponse();
            response.setUserDto(userDto);
            return ServiceResult.createSuccessServiceResult("获取登录信息成功",response);
        } catch (Exception e){
            String message = "获取登录信息失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 用户退出
     */
    @ResponseBody
    @RequestMapping(value = UserApiRoute.EXIT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ExitResponse> exit(HttpServletRequest httpServletRequest, @RequestBody ExitRequest request){
        try {
            SessionUtil.clearLoginUser(httpServletRequest);
            ExitResponse response = new ExitResponse();
            return ServiceResult.createSuccessServiceResult("用户退出成功",response);
        } catch (Exception e){
            String message = "用户退出失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 修改用户信息
     */
    @ResponseBody
    @RequestMapping(value = UserApiRoute.UPDATE_USER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateUserResponse> updateUser(@RequestBody UpdateUserRequest request){
        try {
            UserDto userDto = request.getUserDto();
            if(Strings.isNullOrEmpty(userDto.getUserName())){
                return ServiceResult.createFailServiceResult("用户名不能为空");
            }
            if(Strings.isNullOrEmpty(userDto.getPassword())){
                return ServiceResult.createFailServiceResult("密码不能为空");
            }
            userService.updateUser(userDto);
            UpdateUserResponse response = new UpdateUserResponse();
            return ServiceResult.createSuccessServiceResult("更新用户成功",response);
        } catch (Exception e){
            String message = "更新用户失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}