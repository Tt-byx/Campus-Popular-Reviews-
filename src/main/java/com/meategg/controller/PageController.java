package com.meategg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String homePage() {
        return "redirect:/create-post";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/post")
    public String postPage() {
        return "post";
    }

    @GetMapping("/browse-post")
    public String createPostPage() {
        return "browse-post";
    }
    
    @GetMapping("/post-detail")
    public String postDetailPage() {
        return "post-detail";
    }
}
//
