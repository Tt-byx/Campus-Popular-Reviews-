package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
