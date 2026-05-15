package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Tag(name = "通知模块", description = "消息通知功能")
@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Resource
    private NotificationService notificationService;

    @Operation(summary = "通知列表")
    @GetMapping("/list")
    public Result<?> listNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.fail(401, "请先登录");
        return notificationService.listNotifications(userId, page, size);
    }

    @Operation(summary = "标记单条已读")
    @PutMapping("/read/{id}")
    public Result<?> markAsRead(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.fail(401, "请先登录");
        return notificationService.markAsRead(userId, id);
    }

    @Operation(summary = "全部已读")
    @PutMapping("/read-all")
    public Result<?> markAllAsRead(HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.fail(401, "请先登录");
        return notificationService.markAllAsRead(userId);
    }

    @Operation(summary = "未读通知数")
    @GetMapping("/unread-count")
    public Result<?> getUnreadCount(HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.ok(java.util.Map.of("count", 0));
        return notificationService.getUnreadCount(userId);
    }

    private Long getUserId(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) return Long.valueOf(String.valueOf(userIdAttr));
        return null;
    }
}
