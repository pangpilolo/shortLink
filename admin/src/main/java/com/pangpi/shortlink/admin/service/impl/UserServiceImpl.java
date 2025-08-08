package com.pangpi.shortlink.admin.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pangpi.shortlink.admin.common.biz.user.UserContext;
import com.pangpi.shortlink.admin.common.constant.RedisCacheConstant;
import com.pangpi.shortlink.admin.service.GroupService;
import com.pangpi.shortlink.convention.exception.ClientException;
import com.pangpi.shortlink.convention.exception.ServiceException;
import com.pangpi.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.pangpi.shortlink.admin.dao.entity.UserDO;
import com.pangpi.shortlink.admin.dao.mapper.UserMapper;
import com.pangpi.shortlink.admin.dto.req.UserLoginReqDTO;
import com.pangpi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.pangpi.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.pangpi.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.pangpi.shortlink.admin.dto.resp.UserRespDTO;
import com.pangpi.shortlink.admin.service.UserService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pangpi.shortlink.convention.util.BeanCopyUtils;
import com.pangpi.shortlink.convention.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.pangpi.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;

/**
 * (User)表服务实现类
 *
 * @author pangpi
 * @since 2024-06-23 20:39:35
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final GroupService groupService;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, username);
        UserDO userDO = getOne(queryWrapper);
        if (ObjUtil.isEmpty(userDO)) {
            throw new ServiceException(UserErrorCodeEnum.USER_NOT_FOUND);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userRespDTO);
        return userRespDTO;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void registerUser(UserRegisterReqDTO requestParam) {
        // 检查用户名是否存在
        if (hasUsername(requestParam.getUsername())) {
            throw new ServiceException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        if (!lock.tryLock()) {
            // 没抢到锁就抛异常
            throw new ServiceException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        // 根据用户名上锁
        try {
            // 存数据库
            UserDO userDO = new UserDO();
            BeanUtils.copyProperties(requestParam, userDO);
            // 检查注册的实体参数是否异常
            checkParam(userDO);
            int insert = baseMapper.insert(userDO);
            if (insert < 1) {
                throw new ServiceException(UserErrorCodeEnum.USER_SAVE_ERROR);
            }
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            // 给新用户添加默认分组
            groupService.addGroup(requestParam.getUsername(),"默认分组");
        } catch (DuplicateKeyException e) {
            throw new ServiceException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void updateUserByUsername(UserUpdateReqDTO requestParam) {
        if (!ObjUtil.equals(UserContext.getUsername(), requestParam.getUsername())) {
            throw new ClientException("不能修改除了本用户外的用户信息");
        }
        // 检查用户名是否存在
        if (StrUtil.isBlank(requestParam.getUsername()) || !hasUsername(requestParam.getUsername())) {
            throw new ServiceException(UserErrorCodeEnum.USER_NAME_EMPTY);
        }
        LambdaUpdateWrapper<UserDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanCopyUtils.copyBean(requestParam,UserDO.class), updateWrapper);
    }


    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        // 根据用户名和密码查询用户，且不能是注销的用户
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO user = getOne(queryWrapper);
        if (ObjUtil.isEmpty(user)) {
            throw new ServiceException(UserErrorCodeEnum.USER_NOT_FOUND);
        }
        // 检查用户是否已经登录
        if (isLogin(user.getUsername())) {
            // 如果已经登录了，那么进行一个key的延迟期限
            stringRedisTemplate.expire(USER_LOGIN_KEY + user.getUsername(), 30L, TimeUnit.MINUTES);
            String token = JWTUtils.createJWT(user.getUsername());
            return new UserLoginRespDTO(token);
        }
        // 用户名上锁
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_LOGIN_KEY + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                // 生成token
                String token = JWTUtils.createJWT(user.getUsername());
                // 存储用户信息到redis
                stringRedisTemplate.opsForValue().set(USER_LOGIN_KEY + user.getUsername(),
                        JSON.toJSONString(user),
                        30L,
                        TimeUnit.MINUTES);
                return new UserLoginRespDTO(token);
            }
        } finally {
            lock.unlock();
        }
        throw new ServiceException(UserErrorCodeEnum.USER_LOGIN_ERROR);
    }

    @Override
    public Boolean checkLogin(String token) {
        if (StrUtil.isBlank(token)) {
            throw new ServiceException("token错误");
        }
        // 解析token
        String username = JWTUtils.getUsername(token);
        return isLogin(username);
    }

    @Override
    public Boolean logout(String token) {
        if (StrUtil.isBlank(token)) {
            throw new ServiceException("token错误");
        }
        // 解析token
        String username = JWTUtils.getUsername(token);
        if (isLogin(username)) {
            // 如果登录咋删除redis缓存
            stringRedisTemplate.delete(USER_LOGIN_KEY + username);
            return true;
        }
        throw new ServiceException(UserErrorCodeEnum.USER_NOT_FOUND_OR_NOT_LOGIN);
    }

    public Boolean isLogin(String username) {
        Boolean login = stringRedisTemplate.hasKey(USER_LOGIN_KEY + username);
        return login != null && login;
    }

    public void checkParam(UserDO userDO) {
        if (StrUtil.isBlank(userDO.getUsername())) {
            throw new ServiceException(UserErrorCodeEnum.USER_NAME_EMPTY);
        }
        if (StrUtil.isBlank(userDO.getPassword())) {
            throw new ServiceException(UserErrorCodeEnum.USER_PASSWORD_EMPTY);
        }
        if (StrUtil.isBlank(userDO.getPhone())) {
            throw new ServiceException(UserErrorCodeEnum.USER_PHONE_EMPTY);
        }
        if (StrUtil.isBlank(userDO.getMail())) {
            throw new ServiceException(UserErrorCodeEnum.USER_EMAIL_EMPTY);
        }
    }
}
