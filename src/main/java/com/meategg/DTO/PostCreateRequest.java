package com.meategg.DTO;

import lombok.Data;

@Data
public class PostCreateRequest {
    private String title;
    private String content;
    private Integer score;
    private String tag;
}
