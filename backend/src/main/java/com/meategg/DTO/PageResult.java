package com.meategg.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "分页响应结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    @Schema(description = "状态码", example = "200")
    private Integer code = 200;

    @Schema(description = "响应消息", example = "操作成功")
    private String message = "操作成功";

    @Schema(description = "数据列表")
    private List<T> data;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Integer page;

    @Schema(description = "每页大小")
    private Integer size;

    @Schema(description = "总页数")
    private Integer totalPages;

    public static <T> PageResult<T> of(List<T> data, long total, int page, int size) {
        PageResult<T> result = new PageResult<>();
        result.setData(data);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setTotalPages((int) Math.ceil((double) total / size));
        return result;
    }
}
