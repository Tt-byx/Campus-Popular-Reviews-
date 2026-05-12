package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "私信消息")
@TableName("message")
@Data
public class Message {
    @Schema(description = "ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "发送者用户ID")
    @TableField("sender_id")
    private Long senderId;

    @Schema(description = "接收者用户ID")
    @TableField("receiver_id")
    private Long receiverId;

    @Schema(description = "消息内容")
    @TableField("content")
    private String content;

    @Schema(description = "是否已读")
    @TableField("is_read")
    private Boolean isRead;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
