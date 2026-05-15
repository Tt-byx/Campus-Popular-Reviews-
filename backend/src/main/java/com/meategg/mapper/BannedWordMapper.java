package com.meategg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meategg.entity.BannedWord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BannedWordMapper extends BaseMapper<BannedWord> {
}
