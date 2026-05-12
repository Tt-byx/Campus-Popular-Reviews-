package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "点赞模块", description = "帖子和评论的点赞功能")
@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Resource
    private LikeService likeService;

    @Operation(summary = "切换点赞状态")
    @PostMapping
    public Result<?> toggleLike(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.fail(401, "请先登录");
        String targetType = (String) request.get("targetType");
        Long targetId = ((Number) request.get("targetId")).longValue();
        return likeService.toggleLike(userId, targetType, targetId);
    }

    @Operation(summary = "查询点赞状态")
    @GetMapping("/status")
    public Result<?> getLikeStatus(@RequestParam String targetType, @RequestParam Long targetId, HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        return likeService.getLikeStatus(userId, targetType, targetId);
    }

    @Operation(summary = "获取点赞数")
    @GetMapping("/count")
    public Result<?> getLikeCount(@RequestParam String targetType, @RequestParam Long targetId) {
        return likeService.getLikeCount(targetType, targetId);
    }

    private Long getUserId(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) return Long.valueOf(String.valueOf(userIdAttr));
        return null;
    }
}
