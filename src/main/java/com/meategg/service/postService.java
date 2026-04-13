package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.Post;
import com.meategg.entity.Result;

public interface postService extends IService<Post> {
    Result createPost(PostCreateRequest request, String username);

    Result listPosts();
    
    Result getPostById(Long id);
    
    Result addComment(Long postId, String username, String content);
    
    Result getCommentsByPostId(Long postId);
}
