package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.userService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class LoginController {
@Resource
private userService userservice;

    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return Result.fail("用户名和密码不能为空");
        }
        return userservice.login(username, password);
    }

    @PostMapping("/create")
    public Result register(@RequestParam String username, @RequestParam String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return Result.fail("用户名和密码不能为空");
        }
        if (password.length() < 6) {
            return Result.fail("密码长度至少为 6 位");
        }
        return userservice.register(username, password);
    }
}
