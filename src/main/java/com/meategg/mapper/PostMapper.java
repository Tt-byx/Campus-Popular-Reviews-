package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    List<Post> searchByTitle(@Param("keyword") String keyword);
}
//1
