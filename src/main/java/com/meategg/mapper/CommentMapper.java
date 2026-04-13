package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("select * from comment where post_id = #{postId} order by created_at desc")
    List<Comment> getCommentsByPostId(Long postId);
}