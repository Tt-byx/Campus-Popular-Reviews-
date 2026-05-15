package com.meategg.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "登录/注册请求参数")
@Data
public class LoginRequest {
    @Schema(description = "用户名", example = "zhangsan", required = true)
    private String username;

    @Schema(description = "密码", example = "123456", required = true)
    private String password;
}
