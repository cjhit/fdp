package com.github.cjhit.fdp.core;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：PageResult.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2020-04-22
 *
 */
@Data
public class PageResult<T> {
    @ApiModelProperty(value = "总数据量", example = "100")
    private int totalCount;//总数据量
    @ApiModelProperty(value = "实体数据列表")
    private List<T> items = new ArrayList<T>();
    @ApiModelProperty(value = "总页数")
    private int pageCount;//总页数
    @ApiModelProperty(hidden = true)
    private int pageSize;//每页数据量
    @ApiModelProperty(hidden = true)
    private int currentPage;//当前页数

    public PageResult(int totalCount, List<T> items, int pageSize, int currentPage) {
        this.totalCount = totalCount;
        this.items = items;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.pageCount = calcPageCount();
    }

    public int calcPageCount() {
        int pageCount = 0;
        if (pageSize != 0) {
            pageCount = totalCount / pageSize;
            if (totalCount % pageSize != 0)
                pageCount++;
        }

        return pageCount;
    }

}
