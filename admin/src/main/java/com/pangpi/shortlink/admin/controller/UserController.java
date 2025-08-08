package com.pangpi.shortlink.admin.controller;


import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import com.pangpi.shortlink.admin.dto.req.UserLoginReqDTO;
import com.pangpi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.pangpi.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.pangpi.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.pangpi.shortlink.admin.dto.resp.UserRespDTO;
import com.pangpi.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制层
 */
@RestController
@RequestMapping("/api/shortlink/admin")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }


    /**
     * 判断用户名是否存在
     */
    @GetMapping("/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        Boolean has = userService.hasUsername(username);
        return Results.success(has);
    }

    /**
     * 新增用户
     */
    @PostMapping("/v1/user")
    public Result<Void> registerUser(@RequestBody UserRegisterReqDTO requestParam) {
        userService.registerUser(requestParam);
        return Results.success();
    }


    /**
     * 修改用户信息
     */
    @PutMapping("/v1/user")
    public Result<Void> updateUser(@RequestBody UserUpdateReqDTO requestParam) {
        userService.updateUserByUsername(requestParam);
        return Results.success();
    }

    /**
     * 登录
     */
    @PostMapping("/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        UserLoginRespDTO res = userService.login(requestParam);
        return Results.success(res);
    }

    /**
     * 检查是否登录
     */
    @GetMapping("/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("token") String token) {
        Boolean res = userService.checkLogin(token);
        return Results.success(res);
    }

    /**
     * 登出
     */
    @DeleteMapping("/v1/user/logout")
    public Result<Boolean> logout(@RequestParam("token") String token) {
        Boolean res = userService.logout(token);
        return Results.success(res);
    }

}
