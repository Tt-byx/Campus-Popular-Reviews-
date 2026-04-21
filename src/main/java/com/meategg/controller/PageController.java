package com.meategg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String homePage() {
        return "redirect:/browse-post";
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
    public String browsePostPage() {
        return "browse-post";
    }

    @GetMapping("/post-detail")
    public String postDetailPage() {
        return "post-detail";
    }

    @GetMapping("/review-target-detail")
    public String reviewTargetDetailPage() {
        return "review-target-detail";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
}
//1