package com.meategg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meategg.entity.Follow;
import com.meategg.entity.Notification;
import com.meategg.entity.User;
import com.meategg.entity.Result;
import com.meategg.mapper.FollowMapper;
import com.meategg.mapper.NotificationMapper;
import com.meategg.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FollowService {
    @Resource
    private FollowMapper followMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private NotificationMapper notificationMapper;

    @Transactional
    public Result<?> toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            return Result.fail("不能关注自己");
        }
        User target = userMapper.selectById(followingId);
        if (target == null) {
            return Result.fail("用户不存在");
        }

        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowingId, followingId);
        Follow existing = followMapper.selectOne(wrapper);

        if (existing != null) {
            followMapper.deleteById(existing.getId());
            return Result.ok(200, "已取消关注", Map.of("followed", false));
        } else {
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            follow.setCreatedAt(LocalDateTime.now());
            followMapper.insert(follow);
            // 创建通知
            Notification notification = new Notification();
            notification.setUserId(followingId);
            notification.setFromUserId(followerId);
            notification.setType("follow");
            notification.setContent("关注了你");
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationMapper.insert(notification);
            return Result.ok(200, "关注成功", Map.of("followed", true));
        }
    }

    public Result<?> checkFollow(Long followerId, Long followingId) {
        if (followerId == null) {
            return Result.ok(Map.of("followed", false));
        }
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowingId, followingId);
        boolean followed = followMapper.selectCount(wrapper) > 0;
        return Result.ok(Map.of("followed", followed));
    }

    public Result<?> getFollowStats(Long userId) {
        long followers = followMapper.selectCount(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowingId, userId));
        long following = followMapper.selectCount(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, userId));
        return Result.ok(Map.of("followers", followers, "following", following));
    }

    public Result<?> getFollowers(Long userId, int page, int size) {
        Page<Follow> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowingId, userId)
                .orderByDesc(Follow::getCreatedAt);
        Page<Follow> result = followMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> list = result.getRecords().stream().map(f -> {
            User user = userMapper.selectById(f.getFollowerId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", f.getId());
            item.put("createdAt", f.getCreatedAt());
            if (user != null) {
                item.put("userId", user.getId());
                item.put("username", user.getUsername());
                item.put("avatar", user.getAvatar());
                item.put("signature", user.getSignature());
            }
            return item;
        }).collect(Collectors.toList());

        return Result.ok(Map.of(
                "data", list,
                "total", result.getTotal(),
                "page", page,
                "size", size
        ));
    }

    public Result<?> getFollowing(Long userId, int page, int size) {
        Page<Follow> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, userId)
                .orderByDesc(Follow::getCreatedAt);
        Page<Follow> result = followMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> list = result.getRecords().stream().map(f -> {
            User user = userMapper.selectById(f.getFollowingId());
            Map<String, Object> item = new HashMap<>();
            item.put("id", f.getId());
            item.put("createdAt", f.getCreatedAt());
            if (user != null) {
                item.put("userId", user.getId());
                item.put("username", user.getUsername());
                item.put("avatar", user.getAvatar());
                item.put("signature", user.getSignature());
            }
            return item;
        }).collect(Collectors.toList());

        return Result.ok(Map.of(
                "data", list,
                "total", result.getTotal(),
                "page", page,
                "size", size
        ));
    }
}
