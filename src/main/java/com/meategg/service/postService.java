package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.Post;
import com.meategg.entity.Result;

public interface postService extends IService<Post> {
    Result createPost(PostCreateRequest request, String username, org.springframework.web.multipart.MultipartFile image);

    Result listPosts();

    Result getPostById(Long id);

    Result createReviewTarget(Long postId, String targetName, String username);

    Result getReviewTargetsByPostId(Long postId);

    Result getReviewTargetById(Long id);

    Result addComment(Long reviewTargetId, String username, String content, Integer score);

    Result getCommentsByReviewTargetId(Long reviewTargetId);
}
//1