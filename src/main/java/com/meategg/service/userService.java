package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meategg.entity.Result;
import com.meategg.entity.User;

/**
 * 用户服务接口
 * 提供用户登录、注册、个人信息管理等功能
 */
public interface userService extends IService<User> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录结果，成功时包含JWT令牌
     */
    Result login(String username, String password);

    /**
     * 用户注册
     * 注册成功后自动登录，返回JWT令牌
     * @param username 用户名
     * @param password 密码（明文，至少6位）
     * @return 注册结果，成功时包含JWT令牌
     */
    Result register(String username, String password);

    /**
     * 获取用户个人资料
     * @param username 用户名
     * @return 用户资料信息
     */
    Result getProfile(String username);

    /**
     * 更新用户个人资料
     * @param oldUsername 原用户名
     * @param newUsername 新用户名
     * @param signature 个性签名
     * @param avatar 头像URL
     * @return 更新结果
     */
    Result updateProfile(String oldUsername, String newUsername, String signature, String avatar);

    /**
     * 获取所有用户列表（管理员功能）
     * @return 用户列表
     */
    Result listAllUsers();

    /**
     * 禁言用户
     * @param targetUsername 目标用户名
     * @return 操作结果
     */
    Result muteUser(String targetUsername);

    /**
     * 删除用户
     * @param targetUsername 目标用户名
     * @return 操作结果
     */
    Result deleteUser(String targetUsername);

    /**
     * 用户自行注销账号
     * @param username 用户名
     * @return 操作结果
     */
    Result deleteUserAccount(String username);
}
