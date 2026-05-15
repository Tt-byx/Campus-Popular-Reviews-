package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "分享记录")
@TableName("share_record")
@Data
public class ShareRecord {
    @Schema(description = "ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "分享用户ID（null为匿名分享）")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "帖子ID")
    @TableField("post_id")
    private Long postId;

    @Schema(description = "分享类型：link/qrcode/wechat/qq")
    @TableField("share_type")
    private String shareType;

    @Schema(description = "创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;
}
