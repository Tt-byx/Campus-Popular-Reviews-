package com.meategg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meategg.entity.Notification;
import com.meategg.entity.User;
import com.meategg.entity.Result;
import com.meategg.mapper.NotificationMapper;
import com.meategg.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Resource
    private NotificationMapper notificationMapper;
    @Resource
    private UserMapper userMapper;

    public Result<?> listNotifications(Long userId, int page, int size) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        Page<Notification> result = notificationMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> list = result.getRecords().stream().map(n -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", n.getId());
            item.put("type", n.getType());
            item.put("targetType", n.getTargetType());
            item.put("targetId", n.getTargetId());
            item.put("content", n.getContent());
            item.put("isRead", n.getIsRead());
            item.put("createdAt", n.getCreatedAt());
            if (n.getFromUserId() != null) {
                User fromUser = userMapper.selectById(n.getFromUserId());
                if (fromUser != null) {
                    item.put("fromUsername", fromUser.getUsername());
                    item.put("fromAvatar", fromUser.getAvatar());
                }
            }
            return item;
        }).collect(Collectors.toList());

        return Result.ok(Map.of(
                "data", list,
                "total", result.getTotal(),
                "page", page,
                "size", size,
                "totalPages", result.getPages()
        ));
    }

    public Result<?> markAsRead(Long userId, Long notificationId) {
        notificationMapper.update(null, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getId, notificationId)
                .eq(Notification::getUserId, userId)
                .set(Notification::getIsRead, true));
        return Result.ok("已标记为已读");
    }

    public Result<?> markAllAsRead(Long userId) {
        notificationMapper.update(null, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false)
                .set(Notification::getIsRead, true));
        return Result.ok("全部已读");
    }

    public Result<?> getUnreadCount(Long userId) {
        long count = notificationMapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false));
        return Result.ok(Map.of("count", count));
    }
}
