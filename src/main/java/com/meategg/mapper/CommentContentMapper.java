package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.CommentContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentContentMapper extends BaseMapper<CommentContent> {

    List<CommentContent> listByPostIdAndCommentIds(@Param("postId") Long postId, @Param("commentIds") List<Long> commentIds);
}
//