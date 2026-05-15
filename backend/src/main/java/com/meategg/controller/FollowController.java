package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Tag(name = "关注模块", description = "用户关注功能")
@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private FollowService followService;

    @Operation(summary = "切换关注状态")
    @PostMapping("/{userId}")
    public Result<?> toggleFollow(@PathVariable Long userId, HttpServletRequest httpRequest) {
        Long followerId = getUserId(httpRequest);
        if (followerId == null) return Result.fail(401, "请先登录");
        return followService.toggleFollow(followerId, userId);
    }

    @Operation(summary = "检查关注状态")
    @GetMapping("/check/{userId}")
    public Result<?> checkFollow(@PathVariable Long userId, HttpServletRequest httpRequest) {
        Long followerId = getUserId(httpRequest);
        return followService.checkFollow(followerId, userId);
    }

    @Operation(summary = "获取关注统计")
    @GetMapping("/stats/{userId}")
    public Result<?> getFollowStats(@PathVariable Long userId) {
        return followService.getFollowStats(userId);
    }

    @Operation(summary = "粉丝列表")
    @GetMapping("/followers/{userId}")
    public Result<?> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return followService.getFollowers(userId, page, size);
    }

    @Operation(summary = "关注列表")
    @GetMapping("/following/{userId}")
    public Result<?> getFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return followService.getFollowing(userId, page, size);
    }

    private Long getUserId(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) return Long.valueOf(String.valueOf(userIdAttr));
        return null;
    }
}
