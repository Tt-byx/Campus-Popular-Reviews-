package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment_user")
public class CommentUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("review_target_id")
    private Long reviewTargetId;

    @TableField("username")
    private String username;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
//1