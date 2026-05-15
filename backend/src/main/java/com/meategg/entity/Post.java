package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "帖子实体")
@Data
@TableName("post")
public class Post {
    @Schema(description = "帖子ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "发布者用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "帖子标题")
    @TableField("title")
    private String title;

    @Schema(description = "帖子正文内容")
    @TableField("content")
    private String content;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @Schema(description = "配图URL")
    @TableField("image_url")
    private String imageUrl;
    
    @Schema(description = "分类标签")
    @TableField("tag")
    private String tag;

    @Schema(description = "浏览次数")
    @TableField("view_count")
    private Integer viewCount;

    @Schema(description = "点赞数")
    @TableField("like_count")
    private Integer likeCount;

    @Schema(description = "收藏数")
    @TableField("favorite_count")
    private Integer favoriteCount;

    @Schema(description = "评论数（非数据库字段，动态计算）")
    @TableField(exist = false)
    private Integer commentCount;

}
