package com.meategg.controller;

import com.meategg.entity.BannedWord;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.BannedWordMapper;
import com.meategg.mapper.UserMapper;
import com.meategg.service.postService;
import com.meategg.service.userService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "管理模块", description = "管理员后台管理接口，包括用户管理、内容管理、违禁词管理")
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

    @Operation(summary = "用户列表", description = "获取所有注册用户列表，需要管理员权限")
    @ApiResponse(description = "成功返回 data = User[]（所有用户数组）")
    @GetMapping("/users")
    public Result<List<Map<String, Object>>> listUsers(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return userService.listAllUsers();
    }

    @Operation(summary = "禁言用户", description = "对指定用户进行禁言处理，禁言后用户无法发帖、评论和创建评论对象，需要管理员权限")
    @ApiResponse(description = "成功返回 data = null，message = '用户已禁言'")
    @PostMapping("/user/{username}/mute")
    public Result<Object> muteUser(
            @Parameter(description = "要禁言的用户名") @PathVariable String username,
            HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        if (username == null || username.trim().isEmpty()) return Result.fail("用户名不能为空");
        return userService.muteUser(username.trim());
    }

    @Operation(summary = "删除用户", description = "删除指定用户账号，需要管理员权限")
    @ApiResponse(description = "成功返回 data = null，message = '用户已删除'")
    @DeleteMapping("/user/{username}")
    public Result<Object> deleteUser(
            @Parameter(description = "要删除的用户名") @PathVariable String username,
            HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        if (username == null || username.trim().isEmpty()) return Result.fail("用户名不能为空");
        return userService.deleteUser(username.trim());
    }

    @Operation(summary = "升级为管理员", description = "将指定普通用户升级为管理员，仅超级管理员可操作")
    @ApiResponse(description = "成功返回 data = null，message = '已将用户升级为管理员'")
    @PostMapping("/user/{username}/promote")
    public Result<Object> promoteToAdmin(
            @Parameter(description = "要升级的用户名") @PathVariable String username,
            HttpServletRequest request) {
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

    @Operation(summary = "降级管理员", description = "将指定管理员降级为普通用户，仅超级管理员可操作")
    @ApiResponse(description = "成功返回 data = null，message = '已将该管理员降级为普通用户'")
    @PostMapping("/user/{username}/demote")
    public Result<Object> demoteFromAdmin(
            @Parameter(description = "要降级的用户名") @PathVariable String username,
            HttpServletRequest request) {
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

    @Operation(summary = "获取当前角色", description = "获取当前登录管理员的角色信息（admin或super_admin）")
    @ApiResponse(description = "成功返回 data = 'admin' 或 'super_admin'")
    @GetMapping("/role")
    public Result<Object> getCurrentRole(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        Object roleAttr = request.getAttribute("role");
        return Result.ok(roleAttr);
    }

    @Operation(summary = "帖子管理列表", description = "获取所有帖子列表（管理员视图），需要管理员权限")
    @ApiResponse(description = "成功返回 data = Post[]（所有帖子，含用户名）")
    @GetMapping("/posts")
    public Result<List<Map<String, Object>>> listPosts(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.listAllPosts();
    }

    @Operation(summary = "管理员删除帖子", description = "管理员强制删除指定帖子，需要管理员权限")
    @ApiResponse(description = "成功返回 data = null，message = '删除成功'")
    @DeleteMapping("/post/{postId}")
    public Result<Object> deletePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.adminDeletePost(postId);
    }

    @Operation(summary = "评论对象管理列表", description = "获取所有评论对象列表（管理员视图），需要管理员权限")
    @ApiResponse(description = "成功返回 data = ReviewTarget[]（所有评论对象）")
    @GetMapping("/review-targets")
    public Result<List<Map<String, Object>>> listReviewTargets(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.listAllReviewTargets();
    }

    @Operation(summary = "管理员删除评论对象", description = "管理员强制删除指定评论对象，需要管理员权限")
    @ApiResponse(description = "成功返回 data = null，message = '删除成功'")
    @DeleteMapping("/review-target/{targetId}")
    public Result<Object> deleteReviewTarget(
            @Parameter(description = "评论对象ID") @PathVariable Long targetId,
            HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.adminDeleteReviewTarget(targetId);
    }

    @Operation(summary = "评论管理列表", description = "获取所有评论列表（管理员视图），需要管理员权限")
    @ApiResponse(description = "成功返回 data = CommentContent[]（所有评论，含用户名和评分）")
    @GetMapping("/comments")
    public Result<List<Map<String, Object>>> listComments(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.listAllComments();
    }

    @Operation(summary = "管理员删除评论", description = "管理员强制删除指定评论，需要管理员权限")
    @ApiResponse(description = "成功返回 data = null，message = '删除成功'")
    @DeleteMapping("/comment/{commentId}")
    public Result<Object> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        return postService.adminDeleteComment(commentId);
    }

    @Operation(summary = "违禁词列表", description = "获取所有违禁词列表，需要管理员权限")
    @ApiResponse(description = "成功返回 data = BannedWord[]（违禁词数组）")
    @GetMapping("/banned-words")
    public Result<List<BannedWord>> listBannedWords(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        List<BannedWord> words = bannedWordMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<BannedWord>query().orderByDesc("created_at")
        );
        return Result.ok(words);
    }

    @Operation(summary = "添加违禁词", description = "添加新的违禁词到过滤列表，需要管理员权限")
    @ApiResponse(description = "成功返回 data = BannedWord（新增的违禁词对象）")
    @PostMapping("/banned-words")
    public Result<BannedWord> addBannedWord(
            @Parameter(description = "违禁词对象") @RequestBody BannedWord bannedWord,
            HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        if (bannedWord == null || bannedWord.getWord() == null || bannedWord.getWord().trim().isEmpty()) {
            return Result.fail("违禁词不能为空");
        }
        String word = bannedWord.getWord().trim();
        Long count = bannedWordMapper.selectCount(
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

    @Operation(summary = "删除违禁词", description = "根据ID删除指定违禁词，需要管理员权限")
    @ApiResponse(description = "成功返回 data = null，message = '删除成功'")
    @DeleteMapping("/banned-words/{id}")
    public Result<Object> deleteBannedWord(
            @Parameter(description = "违禁词ID") @PathVariable Integer id,
            HttpServletRequest request) {
        if (!isAdmin(request)) return Result.fail(403, "无管理员权限");
        bannedWordMapper.deleteById(id);
        return Result.ok(200, "删除成功", null);
    }
}
