package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "违禁词实体（用于内容过滤）")
@Data
@TableName("banned_word")
public class BannedWord {
    @Schema(description = "违禁词ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "违禁词内容")
    @TableField("word")
    private String word;

    @Schema(description = "添加时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
