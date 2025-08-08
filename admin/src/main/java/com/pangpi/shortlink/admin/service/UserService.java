package com.pangpi.shortlink.admin.service;
 
import com.pangpi.shortlink.admin.dao.entity.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pangpi.shortlink.admin.dto.req.UserLoginReqDTO;
import com.pangpi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.pangpi.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.pangpi.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.pangpi.shortlink.admin.dto.resp.UserRespDTO;


/**
 * (User)表服务接口
 * @author pangpi
 * @since 2024-06-23 20:39:35
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam 注册的用户实体
     */
    void registerUser(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名修改用户信息
     * @param requestParam 用户实体
     */
    void updateUserByUsername(UserUpdateReqDTO requestParam);

    /**
     * 根据用户名密码登录
     * @param requestParam 登录参数
     * @return token信息
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查对应的用户令牌中的用户是否登录
     * @param token 用户令牌
     * @return 是否登录
     */
    Boolean checkLogin(String token);

    /**
     * 退出登录
     * @param token 用户令牌
     * @return 是否退出
     */
    Boolean logout(String token);
}
