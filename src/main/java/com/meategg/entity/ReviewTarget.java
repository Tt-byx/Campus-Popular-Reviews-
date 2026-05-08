package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "评论对象实体（如食堂窗口、课程、社团等被评论的目标）")
@Data
@TableName("review_target")
public class ReviewTarget {
    @Schema(description = "评论对象ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "所属帖子ID")
    @TableField("post_id")
    private Long postId;

    @Schema(description = "评论对象名称")
    @TableField("target_name")
    private String targetName;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "评论数（非数据库字段，动态计算）")
    @TableField(exist = false)
    private Integer commentCount;

}
