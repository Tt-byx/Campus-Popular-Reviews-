package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
