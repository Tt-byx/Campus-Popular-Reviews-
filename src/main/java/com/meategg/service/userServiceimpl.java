package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meategg.Utils.JwtUtils;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.annotation.Resource;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.query;

public class userServiceimpl extends ServiceImpl<UserMapper, User> implements userService {
@Resource
private JwtUtils jwtUtils;

    @Override
    public Result login(String username, String password) {
        User user = query().eq("username", username).one();
    if(user == null){return Result.fail("用户名或密码错误");}
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.fail("用户名或密码错误");
        }
    String jwt = jwtUtils.createJwt(username);
    return Result.ok(jwt);
    }
}
