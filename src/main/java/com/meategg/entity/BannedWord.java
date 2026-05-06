package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("banned_word")
public class BannedWord {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("word")
    private String word;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
