package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "评论用户关联实体（记录谁在哪个评论对象下发表了评论）")
@Data
@TableName("comment_user")
public class CommentUser {
    @Schema(description = "评论记录ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "所属评论对象ID")
    @TableField("review_target_id")
    private Long reviewTargetId;

    @Schema(description = "评论者用户名")
    @TableField("username")
    private String username;

    @Schema(description = "评论时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
