package com.meategg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meategg.entity.Message;
import com.meategg.entity.User;
import com.meategg.entity.Result;
import com.meategg.mapper.MessageMapper;
import com.meategg.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    public Result<?> sendMessage(Long senderId, Long receiverId, String content) {
        if (senderId.equals(receiverId)) {
            return Result.fail("不能给自己发消息");
        }
        User receiver = userMapper.selectById(receiverId);
        if (receiver == null) {
            return Result.fail("接收者不存在");
        }
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setIsRead(false);
        msg.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(msg);
        return Result.ok(200, "发送成功", Map.of("id", msg.getId()));
    }

    public Result<?> getConversations(Long userId) {
        // 获取所有与该用户相关的最新消息
        List<Message> allMessages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getSenderId, userId)
                        .or()
                        .eq(Message::getReceiverId, userId)
                        .orderByDesc(Message::getCreatedAt));

        // 按对话方分组，取每组最新一条
        Map<Long, Message> conversationMap = new LinkedHashMap<>();
        for (Message msg : allMessages) {
            Long otherId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            conversationMap.putIfAbsent(otherId, msg);
        }

        List<Map<String, Object>> conversations = new ArrayList<>();
        for (Map.Entry<Long, Message> entry : conversationMap.entrySet()) {
            Long otherId = entry.getKey();
            Message lastMsg = entry.getValue();
            User other = userMapper.selectById(otherId);
            if (other == null) continue;

            // 未读消息数
            long unread = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                    .eq(Message::getSenderId, otherId)
                    .eq(Message::getReceiverId, userId)
                    .eq(Message::getIsRead, false));

            Map<String, Object> item = new HashMap<>();
            item.put("userId", other.getId());
            item.put("username", other.getUsername());
            item.put("avatar", other.getAvatar());
            item.put("lastMessage", lastMsg.getContent());
            item.put("lastTime", lastMsg.getCreatedAt());
            item.put("unread", unread);
            conversations.add(item);
        }

        // 按最新消息时间排序
        conversations.sort((a, b) -> {
            LocalDateTime ta = (LocalDateTime) a.get("lastTime");
            LocalDateTime tb = (LocalDateTime) b.get("lastTime");
            return tb.compareTo(ta);
        });

        return Result.ok(conversations);
    }

    public Result<?> getConversation(Long userId, Long otherId, int page, int size) {
        Page<Message> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .and(w -> w
                        .and(inner -> inner.eq(Message::getSenderId, userId).eq(Message::getReceiverId, otherId))
                        .or(inner -> inner.eq(Message::getSenderId, otherId).eq(Message::getReceiverId, userId)))
                .orderByDesc(Message::getCreatedAt);
        Page<Message> result = messageMapper.selectPage(pageParam, wrapper);

        // 标记为已读
        messageMapper.update(null, new LambdaUpdateWrapper<Message>()
                .eq(Message::getSenderId, otherId)
                .eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, false)
                .set(Message::getIsRead, true));

        User other = userMapper.selectById(otherId);
        List<Map<String, Object>> list = result.getRecords().stream().map(m -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("content", m.getContent());
            item.put("isMine", m.getSenderId().equals(userId));
            item.put("createdAt", m.getCreatedAt());
            return item;
        }).collect(Collectors.toList());

        // 反转为时间正序
        Collections.reverse(list);

        return Result.ok(Map.of(
                "data", list,
                "total", result.getTotal(),
                "page", page,
                "size", size,
                "other", other != null ? Map.of("id", other.getId(), "username", other.getUsername(), "avatar", other.getAvatar() != null ? other.getAvatar() : "") : Map.of()
        ));
    }

    public Result<?> markAsRead(Long userId, Long otherId) {
        messageMapper.update(null, new LambdaUpdateWrapper<Message>()
                .eq(Message::getSenderId, otherId)
                .eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, false)
                .set(Message::getIsRead, true));
        return Result.ok("已标记为已读");
    }

    public Result<?> getUnreadCount(Long userId) {
        long count = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, false));
        return Result.ok(Map.of("count", count));
    }
}
