package com.meategg.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "登录/注册响应结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT访问令牌")
    private String token;

    @Schema(description = "令牌类型（固定为Bearer）")
    private String tokenType;

    @Schema(description = "令牌过期时间（秒）")
    private Long expiresIn;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户角色")
    private String role;
}
