package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.userService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.query;

@Controller
@RequestMapping("/user")
public class LoginController {
@Resource
private userService userservice;
    @PostMapping("/login")
    public Result login(String username, String password)
    {return userservice.login(username,password);}

    @PostMapping("/create")


}
