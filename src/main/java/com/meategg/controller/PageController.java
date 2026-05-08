package com.meategg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "页面路由", description = "前端页面跳转路由")
@Controller
public class PageController {

    @Operation(summary = "首页", description = "重定向到帖子浏览页")
    @ApiResponse(description = "重定向到 /browse-post")
    @GetMapping("/")
    public String homePage() {
        return "redirect:/browse-post";
    }

    @Operation(summary = "登录页", description = "跳转到登录页面")
    @ApiResponse(description = "返回 login 页面")
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @Operation(summary = "注册页", description = "跳转到注册页面")
    @ApiResponse(description = "返回 register 页面")
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @Operation(summary = "发帖页", description = "跳转到发布帖子页面")
    @ApiResponse(description = "返回 post 页面")
    @GetMapping("/post")
    public String postPage() {
        return "post";
    }

    @Operation(summary = "帖子浏览页", description = "跳转到帖子浏览首页")
    @ApiResponse(description = "返回 browse-post 页面")
    @GetMapping("/browse-post")
    public String browsePostPage() {
        return "browse-post";
    }

    @Operation(summary = "帖子详情页", description = "跳转到帖子详情页面")
    @ApiResponse(description = "返回 post-detail 页面")
    @GetMapping("/post-detail")
    public String postDetailPage() {
        return "post-detail";
    }

    @Operation(summary = "评论对象详情页", description = "跳转到评论对象详情页面")
    @ApiResponse(description = "返回 review-target-detail 页面")
    @GetMapping("/review-target-detail")
    public String reviewTargetDetailPage() {
        return "review-target-detail";
    }

    @Operation(summary = "个人中心", description = "跳转到个人中心页面")
    @ApiResponse(description = "返回 profile 页面")
    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }

    @Operation(summary = "管理中心", description = "跳转到管理员后台页面")
    @ApiResponse(description = "返回 admin 页面")
    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @Operation(summary = "分类页", description = "跳转到帖子分类浏览页面")
    @ApiResponse(description = "返回 categories 页面")
    @GetMapping("/categories")
    public String categoriesPage() {
        return "categories";
    }

    @Operation(summary = "排行页", description = "跳转到帖子排行榜页面")
    @ApiResponse(description = "返回 ranking 页面")
    @GetMapping("/ranking")
    public String rankingPage() {
        return "ranking";
    }

    @Operation(summary = "搜索页", description = "跳转到搜索页面")
    @ApiResponse(description = "返回 search 页面")
    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    @Operation(summary = "设置页", description = "跳转到系统设置页面")
    @ApiResponse(description = "返回 settings 页面")
    @GetMapping("/settings")
    public String settingsPage() {
        return "settings";
    }
}
