package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "评论内容实体（存储评论的具体内容和评分）")
@Data
@TableName("comment_content")
public class CommentContent {
    @Schema(description = "评论内容ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "所属评论对象ID")
    @TableField("review_target_id")
    private Long reviewTargetId;

    @Schema(description = "关联的评论记录ID")
    @TableField("comment_id")
    private Long commentId;

    @Schema(description = "评论正文内容")
    @TableField("content")
    private String content;

    @Schema(description = "评分（1-5分）")
    @TableField("score")
    private Integer score;
}
