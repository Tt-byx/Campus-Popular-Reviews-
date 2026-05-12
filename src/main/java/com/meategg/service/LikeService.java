package com.meategg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meategg.entity.LikeRecord;
import com.meategg.entity.Notification;
import com.meategg.entity.Post;
import com.meategg.entity.Result;
import com.meategg.mapper.LikeRecordMapper;
import com.meategg.mapper.NotificationMapper;
import com.meategg.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class LikeService {
    @Resource
    private LikeRecordMapper likeRecordMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private NotificationMapper notificationMapper;

    @Transactional
    public Result<?> toggleLike(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId);
        LikeRecord existing = likeRecordMapper.selectOne(wrapper);

        if (existing != null) {
            likeRecordMapper.deleteById(existing.getId());
            if ("post".equals(targetType)) {
                postMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Post>()
                        .eq(Post::getId, targetId)
                        .setSql("like_count = like_count - 1"));
            }
            return Result.ok(200, "已取消点赞", Map.of("liked", false));
        } else {
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setTargetType(targetType);
            record.setTargetId(targetId);
            record.setCreatedAt(LocalDateTime.now());
            likeRecordMapper.insert(record);
            if ("post".equals(targetType)) {
                postMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Post>()
                        .eq(Post::getId, targetId)
                        .setSql("like_count = like_count + 1"));
                // 创建通知
                Post post = postMapper.selectById(targetId);
                if (post != null && !post.getUserId().equals(userId)) {
                    Notification notification = new Notification();
                    notification.setUserId(post.getUserId());
                    notification.setFromUserId(userId);
                    notification.setType("like");
                    notification.setTargetType("post");
                    notification.setTargetId(targetId);
                    notification.setContent("赞了你的帖子");
                    notification.setIsRead(false);
                    notification.setCreatedAt(LocalDateTime.now());
                    notificationMapper.insert(notification);
                }
            }
            return Result.ok(200, "点赞成功", Map.of("liked", true));
        }
    }

    public Result<?> getLikeStatus(Long userId, String targetType, Long targetId) {
        if (userId == null) {
            return Result.ok(Map.of("liked", false));
        }
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId);
        boolean liked = likeRecordMapper.selectCount(wrapper) > 0;
        return Result.ok(Map.of("liked", liked));
    }

    public Result<?> getLikeCount(String targetType, Long targetId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId);
        long count = likeRecordMapper.selectCount(wrapper);
        return Result.ok(Map.of("count", count));
    }
}
