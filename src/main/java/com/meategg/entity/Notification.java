package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "通知消息")
@TableName("notification")
@Data
public class Notification {
    @Schema(description = "ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "接收者用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "发送者用户ID（null为系统通知）")
    @TableField("from_user_id")
    private Long fromUserId;

    @Schema(description = "通知类型：like/comment/follow/system")
    @TableField("type")
    private String type;

    @Schema(description = "目标类型：post/comment/review_target")
    @TableField("target_type")
    private String targetType;

    @Schema(description = "目标ID")
    @TableField("target_id")
    private Long targetId;

    @Schema(description = "通知内容")
    @TableField("content")
    private String content;

    @Schema(description = "是否已读")
    @TableField("is_read")
    private Boolean isRead;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
