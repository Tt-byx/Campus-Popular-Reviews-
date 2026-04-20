package com.meategg.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public static Result ok(){
        return new Result(200, "操作成功", null);
    }
    
    public static Result ok(Object data){
        return new Result(200, "操作成功", data);
    }
    
    public static Result ok(Integer code, String message, Object data){
        return new Result(code, message, data);
    }
    
    public static Result fail(String errorMsg){
        return new Result(400, errorMsg, null);
    }
    
    public static Result fail(Integer code, String errorMsg){
        return new Result(code, errorMsg, null);
    }
}
//1
