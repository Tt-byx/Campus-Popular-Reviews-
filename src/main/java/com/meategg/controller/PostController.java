package com.meategg.controller;

import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.Result;
import com.meategg.service.postService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
}
