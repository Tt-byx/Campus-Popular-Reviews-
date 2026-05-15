package com.meategg.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "创建帖子请求参数")
@Data
public class PostCreateRequest {
    @Schema(description = "帖子标题", example = "食堂二楼新窗口测评")
    private String title;

    @Schema(description = "帖子正文内容", example = "今天试了食堂二楼的新窗口...")
    private String content;

    @Schema(description = "分类标签", example = "食堂")
    private String tag;
}
