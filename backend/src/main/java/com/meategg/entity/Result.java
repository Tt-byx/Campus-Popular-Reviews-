package com.meategg.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "统一响应结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    @Schema(description = "状态码：200-成功, 400-请求错误, 401-未登录, 403-无权限", example = "200")
    private Integer code;

    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    public static <T> Result<T> ok() {
        return new Result<T>(200, "操作成功", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<T>(200, "操作成功", data);
    }

    public static <T> Result<T> ok(Integer code, String message, T data) {
        return new Result<T>(code, message, data);
    }

    public static <T> Result<T> fail(String errorMsg) {
        return new Result<T>(400, errorMsg, null);
    }

    public static <T> Result<T> fail(Integer code, String errorMsg) {
        return new Result<T>(code, errorMsg, null);
    }
}
