package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.postService;
import com.meategg.service.userService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private userService userService;

    @Resource
    private postService postService;

    private boolean isAdmin(HttpServletRequest request) {
        Object roleAttr = request.getAttribute("role");
        return "admin".equals(roleAttr);
    }

    @GetMapping("/users")
    public Result listUsers(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return userService.listAllUsers();
    }

    @PostMapping("/user/{username}/mute")
    public Result muteUser(@PathVariable String username, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        if (username == null || username.trim().isEmpty()) return Result.fail("用户名不能为空");
        return userService.muteUser(username.trim());
    }

    @DeleteMapping("/user/{username}")
    public Result deleteUser(@PathVariable String username, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        if (username == null || username.trim().isEmpty()) return Result.fail("用户名不能为空");
        return userService.deleteUser(username.trim());
    }

    @GetMapping("/posts")
    public Result listPosts(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.listAllPosts();
    }

    @DeleteMapping("/post/{postId}")
    public Result deletePost(@PathVariable Long postId, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.deletePost(postId);
    }
}
//1
