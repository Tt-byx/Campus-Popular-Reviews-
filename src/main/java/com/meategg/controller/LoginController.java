package com.meategg.controller;

import com.meategg.DTO.LoginRequest;
import com.meategg.entity.Result;
import com.meategg.service.userService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class LoginController {
@Resource
private userService userservice;

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() || 
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.fail(401, "用户名和密码不能为空");
        }
        return userservice.login(request.getUsername(), request.getPassword());
    }

    @PostMapping("/create")
    public Result register(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() || 
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.fail("用户名和密码不能为空");
        }
        if (request.getPassword().length() < 6) {
            return Result.fail("密码长度至少为 6 位");
        }
        return userservice.register(request.getUsername(), request.getPassword());
    }

}
