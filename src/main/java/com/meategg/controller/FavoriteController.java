package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Tag(name = "收藏模块", description = "帖子收藏功能")
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    @Resource
    private FavoriteService favoriteService;

    @Operation(summary = "切换收藏状态")
    @PostMapping("/{postId}")
    public Result<?> toggleFavorite(@PathVariable Long postId, HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.fail(401, "请先登录");
        return favoriteService.toggleFavorite(userId, postId);
    }

    @Operation(summary = "检查收藏状态")
    @GetMapping("/check/{postId}")
    public Result<?> checkFavorite(@PathVariable Long postId, HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        return favoriteService.checkFavorite(userId, postId);
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/list")
    public Result<?> listFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.fail(401, "请先登录");
        return favoriteService.listFavorites(userId, page, size);
    }

    private Long getUserId(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) return Long.valueOf(String.valueOf(userIdAttr));
        return null;
    }
}
