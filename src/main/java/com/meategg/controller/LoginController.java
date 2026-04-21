package com.meategg.controller;

import com.meategg.DTO.LoginRequest;
import com.meategg.entity.Result;
import com.meategg.service.OssService;
import com.meategg.service.userService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Resource
    private userService userservice;

    @Resource
    private OssService ossService;

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

    @GetMapping("/profile")
    public Result getProfile(HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return userservice.getProfile(username);
    }

    @PostMapping("/profile/update")
    public Result updateProfile(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String oldUsername = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (oldUsername == null) {
            return Result.fail(401, "请先登录");
        }
        
        String newUsername = params.get("username");
        String signature = params.get("signature");
        String avatar = params.get("avatar");
        
        return userservice.updateProfile(oldUsername, newUsername, signature, avatar);
    }

    @PostMapping("/profile/avatar")
    public Result uploadAvatar(@RequestParam("image") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return Result.fail("图片不能为空");
        }
        try {
            String avatarUrl = ossService.uploadAvatar(image);
            return Result.ok(200, "上传成功", avatarUrl);
        } catch (Exception e) {
            return Result.fail("头像上传失败: " + e.getMessage());
        }
    }
}
//1