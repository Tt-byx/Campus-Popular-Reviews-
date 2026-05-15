package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.Post;
import com.meategg.entity.Result;

/**
 * 帖子服务接口
 * 提供帖子的发布、浏览、评论对象管理、评论管理、搜索、排行等核心功能
 */
public interface postService extends IService<Post> {

    /**
     * 创建帖子
     * @param request 帖子创建请求
     * @param username 发布者用户名
     * @param image 帖子配图（可选）
     * @return 创建结果
     */
    Result createPost(PostCreateRequest request, String username, org.springframework.web.multipart.MultipartFile image);

    /**
     * 获取帖子列表（按时间倒序）
     * @return 帖子列表
     */
    Result listPosts();

    /**
     * 获取帖子详情（同时增加浏览次数）
     * @param id 帖子ID
     * @return 帖子详情
     */
    Result getPostById(Long id);

    /**
     * 创建评论对象
     * @param postId 所属帖子ID
     * @param targetName 评论对象名称
     * @param username 创建者用户名
     * @return 创建结果
     */
    Result createReviewTarget(Long postId, String targetName, String username);

    /**
     * 获取帖子下的评论对象列表
     * @param postId 帖子ID
     * @return 评论对象列表
     */
    Result getReviewTargetsByPostId(Long postId);

    /**
     * 获取评论对象详情
     * @param id 评论对象ID
     * @return 评论对象详情
     */
    Result getReviewTargetById(Long id);

    /**
     * 发表评论
     * @param reviewTargetId 评论对象ID
     * @param username 评论者用户名
     * @param content 评论内容
     * @param score 评分（1-5）
     * @return 评论结果
     */
    Result addComment(Long reviewTargetId, String username, String content, Integer score);

    /**
     * 获取评论对象下的评论列表
     * @param reviewTargetId 评论对象ID
     * @return 评论列表
     */
    Result getCommentsByReviewTargetId(Long reviewTargetId);

    /**
     * 获取用户发布的帖子列表
     * @param username 用户名
     * @return 帖子列表
     */
    Result listUserPosts(String username);

    /**
     * 获取用户发表的评论列表
     * @param username 用户名
     * @return 评论列表
     */
    Result listUserComments(String username);

    /**
     * 获取用户创建的评论对象列表
     * @param username 用户名
     * @return 评论对象列表
     */
    Result listUserReviewTargets(String username);

    /**
     * 用户删除自己的帖子
     * @param postId 帖子ID
     * @param username 用户名
     * @return 操作结果
     */
    Result deletePost(Long postId, String username);

    /**
     * 用户删除自己的评论
     * @param commentId 评论ID
     * @param username 用户名
     * @return 操作结果
     */
    Result deleteComment(Long commentId, String username);

    /**
     * 用户删除自己创建的评论对象
     * @param targetId 评论对象ID
     * @param username 用户名
     * @return 操作结果
     */
    Result deleteReviewTarget(Long targetId, String username);

    /**
     * 管理员获取所有帖子列表
     * @return 帖子列表
     */
    Result listAllPosts();

    /**
     * 管理员强制删除帖子
     * @param postId 帖子ID
     * @return 操作结果
     */
    Result adminDeletePost(Long postId);

    /**
     * 管理员强制删除评论对象
     * @param targetId 评论对象ID
     * @return 操作结果
     */
    Result adminDeleteReviewTarget(Long targetId);

    /**
     * 管理员强制删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    Result adminDeleteComment(Long commentId);

    /**
     * 管理员获取所有评论对象列表
     * @return 评论对象列表
     */
    Result listAllReviewTargets();

    /**
     * 管理员获取所有评论列表
     * @return 评论列表
     */
    Result listAllComments();

    /**
     * 获取帖子统计数据
     * @param postId 帖子ID
     * @return 统计数据（浏览量、评论数）
     */
    Result getPostStats(Long postId);

    /**
     * 获取评论对象统计数据
     * @param targetId 评论对象ID
     * @return 统计数据（评论数、平均评分）
     */
    Result getReviewTargetStats(Long targetId);

    /**
     * 按分类标签获取帖子列表
     * @param tag 分类标签
     * @return 帖子列表
     */
    Result listPostsByTag(String tag);

    /**
     * 获取帖子排行榜
     * @param sortType 排序方式：hot-热度, new-最新, comments-最多评论
     * @return 排行列表
     */
    Result listPostsRanking(String sortType);

    /**
     * 获取个性化推荐帖子
     * @param username 用户名（可选，用于偏好分析）
     * @param previousIds 已展示的帖子ID（逗号分隔，用于去重）
     * @return 推荐帖子列表
     */
    Result getPersonalizedRecommendation(String username, String previousIds);

    /**
     * 搜索帖子和评论对象
     * @param keyword 搜索关键词
     * @return 搜索结果（包含帖子和评论对象）
     */
    Result search(String keyword);
}
