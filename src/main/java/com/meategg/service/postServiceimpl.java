package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.CommentContent;
import com.meategg.entity.CommentUser;
import com.meategg.entity.Post;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.CommentContentMapper;
import com.meategg.mapper.CommentUserMapper;
import com.meategg.mapper.PostMapper;
import com.meategg.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class postServiceimpl extends ServiceImpl<PostMapper, Post> implements postService {
    @Resource
    private PostMapper postMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommentUserMapper commentUserMapper;
    @Resource
    private CommentContentMapper commentContentMapper;

    @Override
    public Result createPost(PostCreateRequest request, String username, org.springframework.web.multipart.MultipartFile image) {
        if (request == null) {
            return Result.fail("请求体不能为空");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return Result.fail("标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.fail("内容不能为空");
        }
        if (request.getTitle().trim().length() > 100) {
            return Result.fail("标题长度不能超过 100 字");
        }
        if (username == null || username.trim().isEmpty()) {
            return Result.fail(401, "请先登录");
        }

        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query()
                .eq("username", username.trim())
                .last("limit 1"));
        if (user == null) {
            return Result.fail(401, "当前登录用户不存在");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filename = System.currentTimeMillis() + extension;
                String uploadDir = "C:\\Users\\28182\\Desktop\\java\\Campus-Popular-Reviews-\\src\\main\\resources\\static\\uploads";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                java.io.File dest = new java.io.File(dir, filename);
                image.transferTo(dest);
                imageUrl = "/uploads/" + filename;
            } catch (Exception e) {
                return Result.fail("文件上传失败: " + e.getMessage());
            }
        }

        Post post = new Post();
        post.setUserId(Long.valueOf(user.getId()));
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setTag(request.getTag());
        post.setImageUrl(imageUrl);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.insert(post);

        Map<String, Object> data = new HashMap<>();
        data.put("id", post.getId());
        data.put("title", post.getTitle());
        data.put("content", post.getContent());
        data.put("tag", post.getTag());
        data.put("imageUrl", post.getImageUrl());
        data.put("username", username.trim());
        return Result.ok(200, "发布成功", data);
    }

    @Override
    public Result listPosts() {
        List<Post> posts = postMapper.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.<Post>query().orderByDesc("id"));
        if (posts == null || posts.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        Set<Long> userIds = new HashSet<>();
        for (Post p : posts) {
            if (p.getUserId() != null) {
                userIds.add(p.getUserId());
            }
        }
        Map<Long, String> usernameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) {
                if (u != null && u.getId() != null) {
                    usernameMap.put(Long.valueOf(u.getId()), u.getUsername());
                }
            }
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (Post p : posts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("title", p.getTitle());
            item.put("content", p.getContent());
            item.put("tag", p.getTag());
            item.put("imageUrl", p.getImageUrl());
            item.put("userId", p.getUserId());
            item.put("username", usernameMap.getOrDefault(p.getUserId(), "未知用户"));
            item.put("createdAt", p.getCreatedAt());
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result getPostById(Long id) {
        if (id == null) {
            return Result.fail("帖子ID不能为空");
        }
        Post post = postMapper.selectById(id);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        User user = userMapper.selectById(post.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("id", post.getId());
        data.put("title", post.getTitle());
        data.put("content", post.getContent());
        data.put("tag", post.getTag());
        data.put("imageUrl", post.getImageUrl());
        data.put("userId", post.getUserId());
        data.put("username", user != null ? user.getUsername() : "未知用户");
        data.put("createdAt", post.getCreatedAt());
        return Result.ok(data);
    }

    @Override
    public Result addComment(Long postId, String username, String content) {
        if (postId == null) {
            return Result.fail("帖子ID不能为空");
        }
        if (username == null || username.trim().isEmpty()) {
            return Result.fail(401, "请先登录");
        }
        if (content == null || content.trim().isEmpty()) {
            return Result.fail("评论内容不能为空");
        }
        if (content.trim().length() > 500) {
            return Result.fail("评论长度不能超过500字");
        }
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query()
                .eq("username", username.trim())
                .last("limit 1"));
        if (user == null) {
            return Result.fail(401, "当前登录用户不存在");
        }
        CommentUser commentUser = new CommentUser();
        commentUser.setPostId(postId);
        commentUser.setUsername(username.trim());
        commentUser.setCreatedAt(LocalDateTime.now());
        commentUserMapper.insert(commentUser);

        CommentContent commentContent = new CommentContent();
        commentContent.setPostId(postId);
        commentContent.setCommentId(commentUser.getId());
        commentContent.setContent(content.trim());
        commentContentMapper.insert(commentContent);

        Map<String, Object> data = new HashMap<>();
        data.put("commentId", commentUser.getId());
        data.put("postId", postId);
        data.put("username", commentUser.getUsername());
        data.put("content", commentContent.getContent());
        data.put("createdAt", commentUser.getCreatedAt());
        return Result.ok(200, "评论发表成功", data);
    }

    @Override
    public Result getCommentsByPostId(Long postId) {
        if (postId == null) {
            return Result.fail("帖子ID不能为空");
        }
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        List<CommentUser> commentUsers = commentUserMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                        .eq("post_id", postId)
                        .orderByDesc("created_at")
        );
        List<Map<String, Object>> data = new ArrayList<>();
        if (commentUsers == null || commentUsers.isEmpty()) {
            return Result.ok(data);
        }

        List<Long> commentIds = new ArrayList<>();
        for (CommentUser cu : commentUsers) {
            if (cu.getId() != null) {
                commentIds.add(cu.getId());
            }
        }
        Map<Long, String> contentMap = new HashMap<>();
        if (!commentIds.isEmpty()) {
            List<CommentContent> contents = commentContentMapper.listByPostIdAndCommentIds(postId, commentIds);
            for (CommentContent cc : contents) {
                if (cc != null && cc.getCommentId() != null) {
                    contentMap.put(cc.getCommentId(), cc.getContent());
                }
            }
        }

        for (CommentUser comment : commentUsers) {
            Map<String, Object> item = new HashMap<>();
            item.put("commentId", comment.getId());
            item.put("postId", comment.getPostId());
            item.put("username", comment.getUsername());
            item.put("content", contentMap.getOrDefault(comment.getId(), ""));
            item.put("createdAt", comment.getCreatedAt());
            data.add(item);
        }
        return Result.ok(data);
    }
}
//