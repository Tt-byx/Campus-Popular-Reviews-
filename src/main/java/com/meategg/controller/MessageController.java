package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "私信模块", description = "用户私信功能")
@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    @Operation(summary = "发送私信")
    @PostMapping("/send")
    public Result<?> sendMessage(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long senderId = getUserId(httpRequest);
        if (senderId == null) return Result.fail(401, "请先登录");
        Long receiverId = ((Number) request.get("receiverId")).longValue();
        String content = (String) request.get("content");
        return messageService.sendMessage(senderId, receiverId, content);
    }

    @Operation(summary = "会话列表")
    @GetMapping("/conversations")
    public Result<?> getConversations(HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.fail(401, "请先登录");
        return messageService.getConversations(userId);
    }

    @Operation(summary = "与某人的对话")
    @GetMapping("/conversation/{userId}")
    public Result<?> getConversation(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            HttpServletRequest httpRequest) {
        Long myId = getUserId(httpRequest);
        if (myId == null) return Result.fail(401, "请先登录");
        return messageService.getConversation(myId, userId, page, size);
    }

    @Operation(summary = "标记对话已读")
    @PutMapping("/read/{userId}")
    public Result<?> markAsRead(@PathVariable Long userId, HttpServletRequest httpRequest) {
        Long myId = getUserId(httpRequest);
        if (myId == null) return Result.fail(401, "请先登录");
        return messageService.markAsRead(myId, userId);
    }

    @Operation(summary = "未读消息数")
    @GetMapping("/unread-count")
    public Result<?> getUnreadCount(HttpServletRequest httpRequest) {
        Long userId = getUserId(httpRequest);
        if (userId == null) return Result.ok(Map.of("count", 0));
        return messageService.getUnreadCount(userId);
    }

    private Long getUserId(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) return Long.valueOf(String.valueOf(userIdAttr));
        return null;
    }
}
