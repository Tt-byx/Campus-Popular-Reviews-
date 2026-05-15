package com.meategg.controller;

import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.Result;
import com.meategg.service.postService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "帖子模块", description = "帖子的发布、浏览、评论对象、评论等核心功能接口")
@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private postService postservice;

    @Operation(summary = "发布帖子", description = "创建一篇新帖子，支持上传图片，需要登录")
    @ApiResponse(description = "成功返回 data = { id, title, content, tag, imageUrl, username, createTime }")
    @PostMapping(value = "", consumes = "multipart/form-data")
    public Result<Map<String, Object>> createPost(
            @Parameter(description = "帖子标题") @RequestParam("title") String title,
            @Parameter(description = "帖子正文内容") @RequestParam("content") String content,
            @Parameter(description = "帖子分类标签") @RequestParam(value = "tag", required = false) String tag,
            @Parameter(description = "帖子配图") @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest httpRequest) {
        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
                    && !contentType.equals("image/gif") && !contentType.equals("image/webp"))) {
                return Result.fail("仅支持 JPG / PNG / GIF / WebP 格式的图片");
            }
        }
        Object usernameAttr = httpRequest.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setTag(tag);
        return postservice.createPost(request, username, image);
    }

    @Operation(summary = "帖子列表", description = "获取所有帖子的列表（按时间倒序）")
    @ApiResponse(description = "成功返回 data = Post[]（帖子数组，含 viewCount、commentCount 等字段）")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listPosts() {
        return postservice.listPosts();
    }

    @Operation(summary = "帖子详情", description = "根据帖子ID获取帖子详细信息，同时会增加浏览次数")
    @ApiResponse(description = "成功返回 data = Post（含 viewCount、commentCount、reviewTargets 列表）")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getPostById(
            @Parameter(description = "帖子ID") @PathVariable Long id) {
        return postservice.getPostById(id);
    }

    @Operation(summary = "创建评论对象", description = "为指定帖子创建一个评论对象（如食堂窗口、课程等），需要登录")
    @ApiResponse(description = "成功返回 data = { id, targetName, postId, creatorUsername }")
    @PostMapping("/{id}/review-target")
    public Result<Map<String, Object>> createReviewTarget(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        Object usernameAttr = httpRequest.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        String targetName = request.get("targetName");
        return postservice.createReviewTarget(id, targetName, username);
    }

    @Operation(summary = "获取评论对象列表", description = "获取指定帖子下的所有评论对象")
    @ApiResponse(description = "成功返回 data = ReviewTarget[]（含平均评分 avgScore、评论数 commentCount）")
    @GetMapping("/{id}/review-targets")
    public Result<List<Map<String, Object>>> getReviewTargets(
            @Parameter(description = "帖子ID") @PathVariable Long id) {
        return postservice.getReviewTargetsByPostId(id);
    }

    @Operation(summary = "评论对象详情", description = "根据评论对象ID获取详细信息")
    @ApiResponse(description = "成功返回 data = ReviewTarget（含评论列表 comments）")
    @GetMapping("/review-target/{id}")
    public Result<Map<String, Object>> getReviewTargetById(
            @Parameter(description = "评论对象ID") @PathVariable Long id) {
        return postservice.getReviewTargetById(id);
    }

    @Operation(summary = "发表评论", description = "对指定评论对象发表评论和评分，需要登录")
    @ApiResponse(description = "成功返回 data = CommentContent（含 content、score、username、createTime）")
    @PostMapping("/review-target/{id}/comment")
    public Result<Map<String, Object>> addComment(
            @Parameter(description = "评论对象ID") @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        Object usernameAttr = httpRequest.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        String content = (String) request.get("content");
        Integer score = request.get("score") != null ? ((Number) request.get("score")).intValue() : null;
        return postservice.addComment(id, username, content, score);
    }

    @Operation(summary = "获取评论列表", description = "获取指定评论对象下的所有评论")
    @ApiResponse(description = "成功返回 data = CommentContent[]（评论数组，含用户名、评分、时间）")
    @GetMapping("/review-target/{id}/comments")
    public Result<List<Map<String, Object>>> getComments(
            @Parameter(description = "评论对象ID") @PathVariable Long id) {
        return postservice.getCommentsByReviewTargetId(id);
    }

    @Operation(summary = "我的帖子", description = "获取当前登录用户发布的所有帖子，需要登录")
    @ApiResponse(description = "成功返回 data = Post[]（当前用户的帖子数组）")
    @GetMapping("/user/posts")
    public Result<List<Map<String, Object>>> getUserPosts(HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return postservice.listUserPosts(username);
    }

    @Operation(summary = "我的评论", description = "获取当前登录用户发表的所有评论，需要登录")
    @ApiResponse(description = "成功返回 data = CommentContent[]（当前用户的评论数组）")
    @GetMapping("/user/comments")
    public Result<List<Map<String, Object>>> getUserComments(HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return postservice.listUserComments(username);
    }

    @Operation(summary = "我的评论对象", description = "获取当前登录用户创建的所有评论对象，需要登录")
    @ApiResponse(description = "成功返回 data = ReviewTarget[]（当前用户的评论对象数组）")
    @GetMapping("/user/review-targets")
    public Result<List<Map<String, Object>>> getUserReviewTargets(HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return postservice.listUserReviewTargets(username);
    }

    @Operation(summary = "帖子统计", description = "获取指定帖子的浏览量和评论数统计")
    @ApiResponse(description = "成功返回 data = { viewCount, commentCount }")
    @GetMapping("/{id}/stats")
    public Result<Map<String, Object>> getPostStats(
            @Parameter(description = "帖子ID") @PathVariable Long id) {
        return postservice.getPostStats(id);
    }

    @Operation(summary = "评论对象统计", description = "获取指定评论对象的评论数和平均评分统计")
    @ApiResponse(description = "成功返回 data = { commentCount, avgScore }")
    @GetMapping("/review-target/{id}/stats")
    public Result<Map<String, Object>> getReviewTargetStats(
            @Parameter(description = "评论对象ID") @PathVariable Long id) {
        return postservice.getReviewTargetStats(id);
    }

    @Operation(summary = "按分类获取帖子", description = "根据标签分类获取帖子列表，如食堂、课程、社团、活动等")
    @ApiResponse(description = "成功返回 data = Post[]（指定分类的帖子数组）")
    @GetMapping("/list/by-tag")
    public Result<List<Map<String, Object>>> listPostsByTag(
            @Parameter(description = "分类标签名称") @RequestParam String tag) {
        return postservice.listPostsByTag(tag);
    }

    @Operation(summary = "帖子排行", description = "获取帖子排行榜，支持按热度(hot)、最新(new)、最多评论(comments)排序")
    @ApiResponse(description = "成功返回 data = Post[]（按排序方式排列的帖子数组）")
    @GetMapping("/list/ranking")
    public Result<List<Map<String, Object>>> listPostsRanking(
            @Parameter(description = "排序方式：hot-热度, new-最新, comments-最多评论") @RequestParam(defaultValue = "hot") String sort) {
        return postservice.listPostsRanking(sort);
    }

    @Operation(summary = "个性化推荐", description = "基于用户偏好标签的个性化帖子推荐算法")
    @ApiResponse(description = "成功返回 data = Post[]（推荐帖子数组）")
    @GetMapping("/list/personalized")
    public Result<List<Map<String, Object>>> getPersonalizedRecommendation(
            @Parameter(description = "之前已展示的帖子ID列表（逗号分隔），用于去重") @RequestParam(required = false) String previousIds,
            HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        return postservice.getPersonalizedRecommendation(username, previousIds);
    }

    @Operation(summary = "删除帖子", description = "删除当前登录用户发布的指定帖子，需要登录")
    @ApiResponse(description = "成功返回 data = null，message = '删除成功'")
    @DeleteMapping("/{id}")
    public Result<Object> deletePost(
            @Parameter(description = "帖子ID") @PathVariable Long id,
            HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return postservice.deletePost(id, username);
    }

    @Operation(summary = "删除评论", description = "删除当前登录用户发表的指定评论，需要登录")
    @ApiResponse(description = "成功返回 data = null，message = '删除成功'")
    @DeleteMapping("/comment/{id}")
    public Result<Object> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long id,
            HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return postservice.deleteComment(id, username);
    }

    @Operation(summary = "删除评论对象", description = "删除当前登录用户创建的指定评论对象，需要登录")
    @ApiResponse(description = "成功返回 data = null，message = '删除成功'")
    @DeleteMapping("/review-target/{id}")
    public Result<Object> deleteReviewTarget(
            @Parameter(description = "评论对象ID") @PathVariable Long id,
            HttpServletRequest request) {
        Object usernameAttr = request.getAttribute("username");
        String username = usernameAttr == null ? null : String.valueOf(usernameAttr).trim();
        if (username == null) {
            return Result.fail(401, "请先登录");
        }
        return postservice.deleteReviewTarget(id, username);
    }

    @Operation(summary = "搜索", description = "根据关键词搜索帖子和评论对象")
    @ApiResponse(description = "成功返回 data = { posts: Post[], reviewTargets: ReviewTarget[] }")
    @GetMapping("/search")
    public Result<Map<String, Object>> search(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        return postservice.search(keyword);
    }
}
