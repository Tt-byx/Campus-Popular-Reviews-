package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.CommentContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentContentMapper extends BaseMapper<CommentContent> {

    List<CommentContent> listByReviewTargetIdAndCommentIds(@Param("reviewTargetId") Long reviewTargetId, @Param("commentIds") List<Long> commentIds);
}
//1