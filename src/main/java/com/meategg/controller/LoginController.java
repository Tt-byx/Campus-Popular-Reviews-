package com.meategg.controller;

import com.meategg.DTO.LoginRequest;
import com.meategg.DTO.LoginResponse;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.service.OssService;
import com.meategg.service.userService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "用户模块", description = "用户登录、注册、个人信息管理接口")
@RestController
@RequestMapping("/user")
public class LoginController {
    @Resource
    private userService userservice;

    @Resource
    private OssService ossService;

    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统，返回JWT令牌")
    @ApiResponse(description = "成功返回 data = { token, tokenType, expiresIn, username, role }")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() || 
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.fail(401, "用户名和密码不能为空");
        }
        return userservice.login(request.getUsername(), request.getPassword());
    }

    @Operation(summary = "用户注册", description = "注册新用户账号，注册成功后自动登录并返回JWT令牌")
    @ApiResponse(description = "成功返回 data = { token, tokenType, expiresIn, username, role }")
    @PostMapping("/create")
    public Result<LoginResponse> register(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() || 
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.fail("用户名和密码不能为空");
        }
        if (request.getPassword().length() < 6) {
            return Result.fail("密码长度至少为 6 位");
        }
        return userservice.register(request.getUsername(), request.getPassword());
    }

    @Operation(summary = "获取个人信息", description = "获取当前登录用户的个人资料，需要在请求头中携带JWT令牌")
    @ApiResponse(description = "成功返回 data = User（含 id, username, signature, avatar, role, createTime 等）")
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return userservice.getProfile(username);
    }

    @Operation(summary = "更新个人信息", description = "更新当前登录用户的昵称、个性签名和头像")
    @ApiResponse(description = "成功返回 data = { username, signature, avatar }")
    @PostMapping("/profile/update")
    public Result<Map<String, Object>> updateProfile(@RequestBody Map<String, String> params, HttpServletRequest request) {
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

    @Operation(summary = "上传头像", description = "上传用户头像图片到阿里云OSS，支持JPG/PNG/GIF/WebP格式")
    @ApiResponse(description = "成功返回 data = avatarUrl（图片访问地址）")
    @PostMapping("/profile/avatar")
    public Result<String> uploadAvatar(
            @Parameter(description = "头像图片文件") @RequestParam("image") MultipartFile image,
            HttpServletRequest request) {
        if (image == null || image.isEmpty()) {
            return Result.fail("图片不能为空");
        }
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? "unknown" : String.valueOf(usernameAttr).trim();
        try {
            String avatarUrl = ossService.uploadAvatar(image, username);
            return Result.ok(200, "上传成功", avatarUrl);
        } catch (Exception e) {
            return Result.fail("头像上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "注销账号", description = "永久删除当前登录用户的账号及所有关联数据")
    @ApiResponse(description = "成功返回 data = null，message = '账号已注销'")
    @DeleteMapping("/account")
    public Result<Object> deleteAccount(HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return userservice.deleteUserAccount(username);
    }

    @Operation(summary = "获取用户公开资料", description = "根据用户ID获取公开资料（不含密码）")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        return userservice.getUserPublicProfile(id);
    }

    @Operation(summary = "获取用户帖子列表", description = "根据用户ID获取其发布的所有帖子")
    @GetMapping("/{id}/posts")
    public Result<?> getUserPosts(@PathVariable Long id) {
        return userservice.getUserPosts(id);
    }

    @Operation(summary = "获取用户统计信息", description = "根据用户ID获取帖子数、粉丝数、关注数")
    @GetMapping("/{id}/stats")
    public Result<?> getUserStats(@PathVariable Long id) {
        return userservice.getUserStats(id);
    }
}
