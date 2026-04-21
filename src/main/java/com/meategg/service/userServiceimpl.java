package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meategg.DTO.LoginResponse;
import com.meategg.Utils.JwtUtils;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class userServiceimpl extends ServiceImpl<UserMapper, User> implements userService {
@Resource
private JwtUtils jwtUtils;

    @Override
    public Result login(String username, String password) {
        User user = query().eq("username", username).one();
        if(user == null){
            return Result.fail(401, "用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }
        
        String jwt = jwtUtils.createJwt(username);
        LoginResponse response = new LoginResponse(
            jwt,
            "Bearer",
            jwtUtils.getExpireInSeconds(),
            username
        );
        
        return Result.ok(200, "登录成功", response);
    }

    @Override
    public Result register(String username, String password) {
        User existUser = query().eq("username", username).one();
        if (existUser != null) {
            return Result.fail("用户已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setCreated_at(LocalDateTime.now());
        
        save(user);
        return Result.ok("注册成功");
    }

    @Override
    public Result getProfile(String username) {
        User user = query().eq("username", username).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }
        user.setPassword(null); // 安全起见不返回密码
        return Result.ok(user);
    }

    @Override
    public Result updateProfile(String oldUsername, String newUsername, String signature, String avatar) {
        User user = query().eq("username", oldUsername).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }

        boolean usernameChanged = false;
        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(oldUsername)) {
            User existUser = query().eq("username", newUsername).one();
            if (existUser != null) {
                return Result.fail("用户名已存在");
            }
            user.setUsername(newUsername);
            usernameChanged = true;
        }

        if (signature != null) {
            user.setSignature(signature);
        }

        if (avatar != null) {
            user.setAvatar(avatar);
        }

        updateById(user);
        user.setPassword(null);

        java.util.Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("user", user);

        if (usernameChanged) {
            String newJwt = jwtUtils.createJwt(newUsername);
            responseData.put("token", newJwt);
            responseData.put("tokenType", "Bearer");
            responseData.put("expiresIn", jwtUtils.getExpireInSeconds());
            responseData.put("username", newUsername);
        }

        return Result.ok(200, "更新成功", responseData);
    }
}
//1