package com.meategg.controller;

import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.Result;
import com.meategg.service.postService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private postService postservice;

    @PostMapping("")
    public Result createPost(@RequestBody PostCreateRequest request, HttpServletRequest httpRequest) {
        Object usernameAttr = httpRequest.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        return postservice.createPost(request, username);
    }

    @GetMapping("/list")
    public Result listPosts() {
        return postservice.listPosts();
    }
    
    @GetMapping("/{id}")
    public Result getPostById(@PathVariable Long id) {
        return postservice.getPostById(id);
    }
    
    @PostMapping("/{id}/comment")
    public Result addComment(@PathVariable Long id, @RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        Object usernameAttr = httpRequest.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        String content = request.get("content");
        return postservice.addComment(id, username, content);
    }
    
    @GetMapping("/{id}/comments")
    public Result getCommentsByPostId(@PathVariable Long id) {
        return postservice.getCommentsByPostId(id);
    }
}
