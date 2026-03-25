package com.meategg.service;

import com.meategg.Utils.JwtUtils;
import com.meategg.entity.Result;

import javax.annotation.Resource;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.query;

public class userServiceimpl implements userService {
@Resource
private JwtUtils jwtUtils;
    @Override
    public Result login(String username, String password) {
    User user = query().eq("username", username).eq("password", password);
    if(user == null){return Result.fail("用户名或密码错误");}
    String jwt = jwtUtils.createJwt(username);
    return Result.ok(jwt);
    }
}
