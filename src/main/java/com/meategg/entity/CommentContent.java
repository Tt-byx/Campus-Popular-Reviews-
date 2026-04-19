package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("comment_content")
public class CommentContent {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("review_target_id")
    private Long reviewTargetId;

    @TableField("comment_id")
    private Long commentId;

    @TableField("content")
    private String content;

    @TableField("score")
    private Integer score;
}
//