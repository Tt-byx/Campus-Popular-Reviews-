package com.meategg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meategg.entity.Favorite;
import com.meategg.entity.Post;
import com.meategg.entity.Result;
import com.meategg.mapper.FavoriteMapper;
import com.meategg.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meategg.entity.User;
import com.meategg.mapper.UserMapper;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private UserMapper userMapper;

    @Transactional
    public Result<?> toggleFavorite(Long userId, Long postId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId);
        Favorite existing = favoriteMapper.selectOne(wrapper);

        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            postMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, postId)
                    .setSql("favorite_count = favorite_count - 1"));
            return Result.ok(200, "已取消收藏", Map.of("favorited", false));
        } else {
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setPostId(postId);
            fav.setCreatedAt(LocalDateTime.now());
            favoriteMapper.insert(fav);
            postMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, postId)
                    .setSql("favorite_count = favorite_count + 1"));
            return Result.ok(200, "收藏成功", Map.of("favorited", true));
        }
    }

    public Result<?> checkFavorite(Long userId, Long postId) {
        if (userId == null) {
            return Result.ok(Map.of("favorited", false));
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId);
        boolean favorited = favoriteMapper.selectCount(wrapper) > 0;
        return Result.ok(Map.of("favorited", favorited));
    }

    public Result<?> listFavorites(Long userId, int page, int size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt);
        Page<Favorite> result = favoriteMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> list = result.getRecords().stream().map(fav -> {
            Post post = postMapper.selectById(fav.getPostId());
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", fav.getId());
            item.put("createdAt", fav.getCreatedAt());
            if (post != null) {
                Map<String, Object> postMap = new java.util.HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("userId", post.getUserId());
                postMap.put("title", post.getTitle());
                postMap.put("content", post.getContent());
                postMap.put("tag", post.getTag());
                postMap.put("imageUrl", post.getImageUrl());
                postMap.put("viewCount", post.getViewCount());
                postMap.put("likeCount", post.getLikeCount());
                postMap.put("commentCount", post.getCommentCount());
                postMap.put("createdAt", post.getCreatedAt());
                User author = userMapper.selectById(post.getUserId());
                if (author != null) {
                    postMap.put("username", author.getUsername());
                    postMap.put("avatar", author.getAvatar());
                }
                item.put("post", postMap);
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
}
