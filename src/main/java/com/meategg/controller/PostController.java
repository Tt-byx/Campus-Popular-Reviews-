package com.meategg.controller;

import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.Post;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.PostMapper;
import com.meategg.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private PostMapper postMapper;
    @Resource
    private UserMapper userMapper;

    @PostMapping("")
    public Result createPost(@RequestBody PostCreateRequest request, HttpServletRequest httpRequest) {
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
        if (request.getScore() != null && (request.getScore() < 0 || request.getScore() > 5)) {
            return Result.fail("评分范围必须在 0 到 5");
        }

        Object usernameAttr = httpRequest.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null || username.isEmpty()) {
            return Result.fail(401, "请先登录");
        }
        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query().eq("username", username).last("limit 1"));
        if (user == null) {
            return Result.fail(401, "当前登录用户不存在");
        }

        Post post = new Post();
        post.setUserId(Long.valueOf(user.getId()));
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setScore(request.getScore());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.insert(post);

        Map<String, Object> data = new HashMap<>();
        data.put("id", post.getId());
        data.put("title", post.getTitle());
        data.put("content", post.getContent());
        data.put("score", post.getScore());
        data.put("username", username);
        return Result.ok(200, "发布成功", data);
    }

    @GetMapping("/list")
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
            item.put("score", p.getScore());
            item.put("userId", p.getUserId());
            item.put("username", usernameMap.getOrDefault(p.getUserId(), "未知用户"));
            item.put("createdAt", p.getCreatedAt());
            data.add(item);
        }
        return Result.ok(data);
    }
}
