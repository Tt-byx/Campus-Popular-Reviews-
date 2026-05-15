package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.ReviewTarget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewTargetMapper extends BaseMapper<ReviewTarget> {

    List<ReviewTarget> searchByTargetName(@Param("keyword") String keyword);
}
//1
