package com.meategg.controller;

import com.meategg.entity.BannedWord;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.BannedWordMapper;
import com.meategg.mapper.UserMapper;
import com.meategg.service.postService;
import com.meategg.service.userService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private userService userService;

    @Resource
    private postService postService;

    @Resource
    private BannedWordMapper bannedWordMapper;

    @Resource
    private UserMapper userMapper;

    private boolean isAdmin(HttpServletRequest request) {
        Object roleAttr = request.getAttribute("role");
        return "admin".equals(roleAttr) || "super_admin".equals(roleAttr);
    }

    private boolean isSuperAdmin(HttpServletRequest request) {
        Object roleAttr = request.getAttribute("role");
        return "super_admin".equals(roleAttr);
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

    @PostMapping("/user/{username}/promote")
    public Result promoteToAdmin(@PathVariable String username, HttpServletRequest request) {
        if (!isSuperAdmin(request)) return Result.fail(403, "仅超级管理员可赋予管理员权限");
        if (username == null || username.trim().isEmpty()) return Result.fail("用户名不能为空");
        User user = userMapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query().eq("username", username.trim())
        );
        if (user == null) return Result.fail("用户不存在");
        if ("super_admin".equals(user.getRole())) return Result.fail("无法修改超级管理员权限");
        if ("admin".equals(user.getRole())) return Result.fail("该用户已是管理员");
        user.setRole("admin");
        userMapper.updateById(user);
        return Result.ok(200, "已将用户升级为管理员", null);
    }

    @PostMapping("/user/{username}/demote")
    public Result demoteFromAdmin(@PathVariable String username, HttpServletRequest request) {
        if (!isSuperAdmin(request)) return Result.fail(403, "仅超级管理员可剥夺管理员权限");
        if (username == null || username.trim().isEmpty()) return Result.fail("用户名不能为空");
        User user = userMapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query().eq("username", username.trim())
        );
        if (user == null) return Result.fail("用户不存在");
        if ("super_admin".equals(user.getRole())) return Result.fail("无法修改超级管理员权限");
        if (!"admin".equals(user.getRole())) return Result.fail("该用户不是管理员");
        user.setRole("user");
        userMapper.updateById(user);
        return Result.ok(200, "已将该管理员降级为普通用户", null);
    }

    @GetMapping("/role")
    public Result getCurrentRole(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        Object roleAttr = request.getAttribute("role");
        return Result.ok(roleAttr);
    }

    @GetMapping("/posts")
    public Result listPosts(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.listAllPosts();
    }

    @DeleteMapping("/post/{postId}")
    public Result deletePost(@PathVariable Long postId, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");

        return postService.adminDeletePost(postId);
    }

    @GetMapping("/review-targets")
    public Result listReviewTargets(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.listAllReviewTargets();
    }

    @DeleteMapping("/review-target/{targetId}")
    public Result deleteReviewTarget(@PathVariable Long targetId, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");

        return postService.adminDeleteReviewTarget(targetId);
    }

    @GetMapping("/comments")
    public Result listComments(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.listAllComments();
    }

    @DeleteMapping("/comment/{commentId}")
    public Result deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");

        return postService.adminDeleteComment(commentId);
    }

    @GetMapping("/banned-words")
    public Result listBannedWords(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        List<BannedWord> words = bannedWordMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<BannedWord>query().orderByDesc("created_at")
        );
        return Result.ok(words);
    }

    @PostMapping("/banned-words")
    public Result addBannedWord(@RequestBody BannedWord bannedWord, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        if (bannedWord == null || bannedWord.getWord() == null || bannedWord.getWord().trim().isEmpty()) {
            return Result.fail("违禁词不能为空");
        }
        String word = bannedWord.getWord().trim();
        Integer count = bannedWordMapper.selectCount(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<BannedWord>query().eq("word", word)
        );
        if (count > 0) {
            return Result.fail("该违禁词已存在");
        }
        BannedWord bw = new BannedWord();
        bw.setWord(word);
        bw.setCreatedAt(LocalDateTime.now());
        bannedWordMapper.insert(bw);
        return Result.ok(200, "添加成功", bw);
    }

    @DeleteMapping("/banned-words/{id}")
    public Result deleteBannedWord(@PathVariable Integer id, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        bannedWordMapper.deleteById(id);
        return Result.ok(200, "删除成功", null);
    }
}
//1
