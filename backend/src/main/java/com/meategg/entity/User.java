package com.meategg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户实体")
@TableName("user")
@Data
public class User {
    @Schema(description = "用户ID")
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @Schema(description = "用户名")
    @TableField("username")
    private String username;
    
    @Schema(description = "密码（BCrypt加密）")
    @TableField("password")
    private String password;
    
    @Schema(description = "角色：user-普通用户, admin-管理员, super_admin-超级管理员")
    @TableField("role")
    private String role;
    
    @Schema(description = "状态：active-正常, muted-禁言")
    @TableField("status")
    private String status;
    
    @Schema(description = "注册时间")
    @TableField("created_at")
    private LocalDateTime created_at;

    @Schema(description = "头像URL")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "个性签名")
    @TableField("signature")
    private String signature;
}
