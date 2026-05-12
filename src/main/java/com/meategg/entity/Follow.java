package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "关注关系")
@TableName("follow")
@Data
public class Follow {
    @Schema(description = "ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "关注者用户ID")
    @TableField("follower_id")
    private Long followerId;

    @Schema(description = "被关注者用户ID")
    @TableField("following_id")
    private Long followingId;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
